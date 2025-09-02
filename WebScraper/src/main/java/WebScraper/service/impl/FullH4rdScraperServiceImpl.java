package WebScraper.service.impl;

import WebScraper.constant.CssSelectorsMessages;
import WebScraper.constant.ScraperMessages;
import WebScraper.dto.ProductResponseDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class FullH4rdScraperServiceImpl implements FullH4rdScraperService {
    private static final Logger logger = Logger.getLogger(FullH4rdScraperServiceImpl.class.getName());

    private final FullH4rdScraperRepository repo;
    private final ProductMapper mapper;

    public FullH4rdScraperServiceImpl(FullH4rdScraperRepository repo, ProductMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<ProductResponseDto> updateProducts() {
        List<Product> collected = new ArrayList<>();
        for (String categoryUrl : FullH4rdUtils.getAllCategoryUrls()) {
            try {
                collected.addAll(scrapeCategory(categoryUrl));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error en categoría: " + categoryUrl, e);
            }
        }
        return collected.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /** Pagina una categoría y acumula productos */
    private List<Product> scrapeCategory(String categoryUrl) throws IOException {
        List<Product> acc = new ArrayList<>();
        int page = 1;
        while (true) {
            String url = buildPageUrl(categoryUrl, page);
            logger.info("Procesando URL: " + url);

            Document doc = fetchDocument(url);
            int added = parsePage(doc, acc);

            // si no hubo cards o no hay “siguiente”, cortamos
            if (added == 0 || !hasNextPage(doc)) break;
            page++;
        }
        return acc;
    }

    /** Procesa una página: recorre cards y hace upsert. Devuelve cuántos cards encontró. */
    private int parsePage(Document doc, List<Product> acc) {
        Elements cards = doc.select(CssSelectorsMessages.FULLH4RD_PRODUCT_LIST);
        if (cards.isEmpty()) return 0;

        for (Element card : cards) {
            try {
                upsertProduct(card).ifPresent(acc::add);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error procesando producto individual: " + e.getMessage(), e);
            }
        }
        return cards.size();
    }

    /** Extrae campos del card y hace upsert; devuelve el Product persistido */
    private Optional<Product> upsertProduct(Element card) {
        String title = card.select(CssSelectorsMessages.FULLH4RD_TITLE).text();
        Double price = extractPrice(card);
        String imageUrl = buildFullUrl(card.select(CssSelectorsMessages.FULLH4RD_IMAGE).attr("src"));
        String productUrl = buildFullUrl(card.select(CssSelectorsMessages.FULLH4RD_LINK).attr("href"));

        if (productUrl.isBlank()) return Optional.empty();

        Optional<Product> existing = repo.findByProductUrl(productUrl);
        Product saved;
        if (existing.isPresent()) {
            saved = updateExistingProduct(existing.get(), title, imageUrl, price);
        } else {
            saved = insertNewProduct(title, imageUrl, productUrl, price);
        }
        return Optional.of(saved);
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

    private Product updateExistingProduct(Product p, String title, String imageUrl, Double price) {
        p.setName(title);
        p.setImageUrl(imageUrl);
        p.setPage(ScraperMessages.PAGE_FULLH4RD);

        List<PriceHistory> ph = p.getPriceHistory();
        if (ph.isEmpty() || !price.equals(ph.get(ph.size() - 1).getPrice())) {
            ph.add(new PriceHistory(price, LocalDateTime.now(), "ARS"));
        }
        return repo.save(p);
    }

    private Product insertNewProduct(String title, String imageUrl, String productUrl, Double price) {
        Product p = new Product(title, imageUrl, productUrl, ScraperMessages.PAGE_FULLH4RD);
        p.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "ARS"));
        return repo.save(p);
    }

    private boolean hasNextPage(Document doc) {
        return doc.selectFirst(CssSelectorsMessages.FULLH4RD_NEXT_PAGE) != null;
    }

    private String buildFullUrl(String relativeUrl) {
        return ScraperMessages.BASE_URL_FULLH4RD + (relativeUrl.startsWith("/") ? "" : "/") + relativeUrl;
    }
}
