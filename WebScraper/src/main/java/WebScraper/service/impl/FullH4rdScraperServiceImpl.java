package WebScraper.service.impl;

import WebScraper.constant.CssSelectorsMessages;
import WebScraper.constant.ScraperMessages;
import WebScraper.dto.ProductResponseDto;
import WebScraper.event.PriceDropEvent;
import WebScraper.mapper.ProductMapper;
import WebScraper.model.PriceHistory;
import WebScraper.model.Product;
import WebScraper.repository.FullH4rdScraperRepository;
import WebScraper.service.FullH4rdScraperService;
import WebScraper.utils.FullH4rdUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class FullH4rdScraperServiceImpl implements FullH4rdScraperService {
    private static final Logger logger = Logger.getLogger(FullH4rdScraperServiceImpl.class.getName());
    private final PriceDropPublisher priceDropPublisher;
    private final FullH4rdScraperRepository repo;
    private final ProductMapper mapper;

    public FullH4rdScraperServiceImpl(PriceDropPublisher priceDropPublisher, FullH4rdScraperRepository repo, ProductMapper mapper) {
        this.priceDropPublisher = priceDropPublisher;
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<ProductResponseDto> updateProducts() {
        final String PAGE = ScraperMessages.PAGE_FULLH4RD;
        final LocalDateTime now = LocalDateTime.now();

        List<Product> collected = new ArrayList<>();
        // URLs vistas en esta corrida para desactivar luego lo no visto
        Set<String> seenUrls = new HashSet<>();

        for (String categoryUrl : FullH4rdUtils.getAllCategoryUrls()) {
            try {
                collected.addAll(scrapeCategory(categoryUrl, seenUrls, now));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error en categoría: " + categoryUrl, e);
            }
        }

        // Desactivar productos activos de esta page que NO se vieron en esta corrida
        if (!seenUrls.isEmpty()) {
            List<Product> activos = repo.findAllByPageAndActiveTrue(PAGE);
            List<Product> toDeactivate = new ArrayList<>();
            for (Product p : activos) {
                String url = p.getProductUrl();
                if (url == null || !seenUrls.contains(url)) {
                    p.setActive(false);
                    p.setLastDeactivationDate(now);
                    toDeactivate.add(p);
                }
            }
            if (!toDeactivate.isEmpty()) {
                repo.saveAll(toDeactivate);
            }
        }

        return collected.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /** Pagina una categoría y acumula productos */
    private List<Product> scrapeCategory(String categoryUrl, Set<String> seenUrls, LocalDateTime now) throws IOException {
        List<Product> acc = new ArrayList<>();
        int page = 1;
        while (true) {
            String url = buildPageUrl(categoryUrl, page);
            Document doc = fetchDocument(url);
            int added = parsePage(doc, acc, seenUrls, now);

            // si no hubo cards o no hay “siguiente”, cortamos
            if (added == 0 || !hasNextPage(doc)) break;
            page++;
        }
        return acc;
    }

    /** Procesa una página: recorre cards y hace upsert. Devuelve cuántos cards encontró. */
    private int parsePage(Document doc, List<Product> acc, Set<String> seenUrls, LocalDateTime now) {
        Elements cards = doc.select(CssSelectorsMessages.FULLH4RD_PRODUCT_LIST);
        if (cards.isEmpty()) return 0;

        for (Element card : cards) {
            try {
                upsertProduct(card, seenUrls, now).ifPresent(acc::add);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error procesando producto individual: " + e.getMessage(), e);
            }
        }
        return cards.size();
    }

    /** Extrae campos del card y hace upsert; devuelve el Product persistido */
    private Optional<Product> upsertProduct(Element card, Set<String> seenUrls, LocalDateTime now) {
        String title = card.select(CssSelectorsMessages.FULLH4RD_TITLE).text();
        Double price = extractPrice(card);
        String imageUrl = buildFullUrl(card.select(CssSelectorsMessages.FULLH4RD_IMAGE).attr("src"));
        String productUrl = buildFullUrl(card.select(CssSelectorsMessages.FULLH4RD_LINK).attr("href"));

        if (productUrl.isBlank()) return Optional.empty();

        // marcar como visto
        seenUrls.add(productUrl);

        Optional<Product> existing = repo.findByProductUrl(productUrl);
        Product saved;
        if (existing.isPresent()) {
            saved = updateExistingProduct(existing.get(), title, imageUrl, price, now);
        } else {
            saved = insertNewProduct(title, imageUrl, productUrl, price, now);
        }
        return Optional.of(repo.save(saved));
    }

    private String buildPageUrl(String categoryUrl, int page) {
        return categoryUrl.replaceAll("/\\d+$", "/" + page);
    }

    private Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(ScraperMessages.USER_AGENT)
                .timeout(10000)
                .get();
    }

    private Double extractPrice(Element card) {
        String priceText = card.select(CssSelectorsMessages.FULLH4RD_PRICE).first().ownText();
        if (priceText.contains(",")) priceText = priceText.split(",")[0];
        return Double.valueOf(priceText.replaceAll("[^\\d]", ""));
    }

    private Product updateExistingProduct(Product p, String title, String imageUrl, Double price, LocalDateTime now) {
        p.setName(title);
        p.setImageUrl(imageUrl);
        p.setPage(ScraperMessages.PAGE_FULLH4RD);

        if (!p.isActive()) {
            p.setActive(true);
            p.setLastActivationDate(now);
        }

        // último precio registrado (si hay)
        List<PriceHistory> ph = p.getPriceHistory();
        Double last = (ph.isEmpty() ? null : ph.get(ph.size() - 1).getPrice());

        // PUBLICAR SOLO SI BAJÓ
        if (last != null && price < last) {
            priceDropPublisher.publish(new PriceDropEvent(
                    UUID.randomUUID().toString(),
                    p.getId(),                 // productId
                    p.getName(),               // nombre
                    last,                      // oldPrice
                    price,                     // newPrice
                    System.currentTimeMillis() // ts
            ));
        }

        // Actualizar historial si cambió
        if (last == null || !last.equals(price)) {
            ph.add(new PriceHistory(price, now, "ARS"));
            p.setPrice(price);
        }
        return p;
    }

    private Product insertNewProduct(String title, String imageUrl, String productUrl, Double price, LocalDateTime now) {
        Product p = new Product(title, imageUrl, productUrl, ScraperMessages.PAGE_FULLH4RD);
        p.getPriceHistory().add(new PriceHistory(price, now, "ARS"));

        // Nuevo → activo y con fecha de activación (y fecha de inserción si la usás)
        p.setActive(true);
        p.setLastActivationDate(now);
        p.setDate(now);
        p.setPrice(price);

        return p;
    }

    private boolean hasNextPage(Document doc) {
        return doc.selectFirst(CssSelectorsMessages.FULLH4RD_NEXT_PAGE) != null;
    }

    private String buildFullUrl(String relativeUrl) {
        return ScraperMessages.BASE_URL_FULLH4RD + (relativeUrl.startsWith("/") ? "" : "/") + relativeUrl;
    }
}
