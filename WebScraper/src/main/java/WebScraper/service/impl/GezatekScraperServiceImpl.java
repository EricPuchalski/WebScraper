package WebScraper.service.impl;

import WebScraper.constant.CssSelectorsMessages;
import WebScraper.constant.ScraperMessages;
import WebScraper.dto.ProductResponseDto;
import WebScraper.event.dto.PriceDropDetectedEvent;
import WebScraper.event.producer.PriceDropPublisher;
import WebScraper.mapper.ProductMapper;
import WebScraper.model.PriceHistory;
import WebScraper.model.Product;
import WebScraper.repository.GezatekScraperRepository;
import WebScraper.service.GezatekScraperService;
import WebScraper.utils.GezatekUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Optional;

@Service
public class GezatekScraperServiceImpl implements GezatekScraperService {
    private static final Logger logger = Logger.getLogger(GezatekScraperServiceImpl.class.getName());

    private final GezatekScraperRepository gezatekScraperRepository;
    private final ProductMapper productMapper;
    private final PriceDropPublisher priceDropPublisher;

    public GezatekScraperServiceImpl(GezatekScraperRepository gezatekScraperRepository, ProductMapper productMapper, PriceDropPublisher priceDropPublisher) {
        this.gezatekScraperRepository = gezatekScraperRepository;
        this.productMapper = productMapper;
        this.priceDropPublisher = priceDropPublisher;
    }

    @Override
    public List<ProductResponseDto> updateProducts() {
        List<Product> productList = new ArrayList<>();
        List<String> categories = GezatekUtils.getAllCategoryUrls();

        final LocalDateTime now = LocalDateTime.now();
        final Set<String> seenUrls = new HashSet<>();

        for (String url : categories) {
            try {
                Document doc = fetchDocument(url);
                Elements products = doc.select(CssSelectorsMessages.GEZATEK_PRODUCT_LIST);

                for (Element product : products) {
                    processProductElement(product, productList, seenUrls, now);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error en URL " + url, e);
            }
        }

        //  desactivar los que no se vieron en esta corrida
        this.deactivateNoStockProducts(seenUrls);

        return productMapper.convertToListDto(productList);
    }


    private Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(ScraperMessages.USER_AGENT)
                .timeout(10000)
                .get();
    }

    private void deactivateNoStockProducts(Set<String> urls){
        LocalDateTime now = LocalDateTime.now();
        if (!urls.isEmpty()) {
            List<Product> actives = gezatekScraperRepository.findAllByPageAndActiveTrue(ScraperMessages.PAGE_GEZATEK);
            List<Product> toDeactivate = new ArrayList<>();
            for (Product p : actives) {
                String url = p.getProductUrl();
                if (url == null || !urls.contains(url)) {
                    p.setActive(false);
                    p.setLastDeactivationDate(now);
                    toDeactivate.add(p);
                }
            }
            if (!toDeactivate.isEmpty()) {
                gezatekScraperRepository.saveAll(toDeactivate);
            }
        }
    }

    private void processProductElement(Element product, List<Product> productList,
                                       Set<String> seenUrls, LocalDateTime now) {
        try {
            String title = product.select(CssSelectorsMessages.GEZATEK_TITLE).text();

            Double price = extractPrice(product);

            String imageUrl = product.select(CssSelectorsMessages.GEZATEK_IMAGE).attr("src");

            String productUrl = buildFullUrl(
                    product.select(CssSelectorsMessages.GEZATEK_LINK).attr("href")
            );

            if (productUrl.isBlank() || title.isBlank()) {
                return;
            }

            seenUrls.add(productUrl);

            Optional<Product> existingProduct = gezatekScraperRepository.findByProductUrl(productUrl);

            if (existingProduct.isPresent()) {
                updateExistingProduct(existingProduct.get(), title, imageUrl, price, productList, now);
            } else {
                insertNewProduct(title, imageUrl, productUrl, price, productList, now);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error procesando producto individual: " + e.getMessage(), e);
        }
    }
    private Double extractPrice(Element product) {
        String priceText = product
                .select(CssSelectorsMessages.GEZATEK_PRICE)
                .attr(CssSelectorsMessages.GEZATEK_PRICE);

        if (priceText.isBlank()) {
            return null;
        }

        return Double.valueOf(priceText);
    }
    private String buildFullUrl(String relativeUrl) {
        return ScraperMessages.BASE_URL_GEZATEK + (relativeUrl.startsWith("/") ? "" : "/") + relativeUrl;
    }

    private void updateExistingProduct(Product p, String title, String imageUrl, Double price,
                                       List<Product> productList, LocalDateTime now) {
        p.setName(title);
        p.setImageUrl(imageUrl);
        p.setPage(ScraperMessages.PAGE_GEZATEK);

        // reactivar si estaba inactivo
        if (!p.isActive()) {
            p.setActive(true);
            p.setLastActivationDate(now);
        }

        List<PriceHistory> ph = p.getPriceHistory();
        Double last = (ph.isEmpty() ? null : ph.get(ph.size() - 1).getPrice());

        // publicar SOLO si bajó
        publishIfPriceDropped(p, last, price);

        // actualizar historial si cambió
        if (last == null || !last.equals(price)) {
            this.updatePrice(p, price);
        }

        gezatekScraperRepository.save(p);
        productList.add(p);
    }


    private void insertNewProduct(String title, String imageUrl, String productUrl, Double price,
                                  List<Product> productList, LocalDateTime now) {

        Product nProduct = this.buildProduct(title, imageUrl, productUrl, price);

        nProduct.setActive(true);
        nProduct.setLastActivationDate(now);
        nProduct.setDate(now);
        nProduct.setPrice(price);

        gezatekScraperRepository.save(nProduct);
        productList.add(nProduct);
    }

    private void publishIfPriceDropped(Product p, Double lastPrice, Double newPrice) {
        if (lastPrice != null && newPrice < lastPrice) {
            priceDropPublisher.publish(PriceDropDetectedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .productId(p.getId())
                    .detectedAt(Instant.now().atZone(ZoneId.systemDefault()).toInstant())
                    .build(
                    ));
        }
    }

    private Product buildProduct(String title, String imageUrl, String productUrl, Double price){
        Product nProduct = Product.builder()
                .name(title)
                .imageUrl(imageUrl)
                .productUrl(productUrl)
                .page(ScraperMessages.PAGE_GEZATEK)
                .build();

        nProduct.getPriceHistory().add(
                PriceHistory.builder()
                        .price(price)
                        .date(LocalDateTime.now())
                        .currency(ScraperMessages.CURRENCY_ARS)
                        .build());

        return nProduct;

    }

    private void updatePrice(Product p, Double price){
        p.getPriceHistory().add(
                PriceHistory
                        .builder()
                        .price(price)
                        .date(LocalDateTime.now())
                        .currency(ScraperMessages.CURRENCY_ARS)
                        .build()
        );
        p.setPrice(price);
    }
}
