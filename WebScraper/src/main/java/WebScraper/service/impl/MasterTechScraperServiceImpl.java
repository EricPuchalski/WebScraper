package WebScraper.service.impl;

import WebScraper.constant.CssSelectorsMessages;
import WebScraper.constant.ScraperMessages;
import WebScraper.dto.ProductResponseDto;
import WebScraper.mapper.ProductMapper;
import WebScraper.model.PriceHistory;
import WebScraper.model.Product;
import WebScraper.repository.MasterTechScraperRepository;
import WebScraper.service.MasterTechScraperService;
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

@Service
public class MasterTechScraperServiceImpl implements MasterTechScraperService {
    private static final Logger logger = Logger.getLogger(MasterTechScraperServiceImpl.class.getName());

    private final MasterTechScraperRepository masterTechScraperRepository;
    private final ProductMapper productMapper;

    public MasterTechScraperServiceImpl(MasterTechScraperRepository masterTechScraperRepository, ProductMapper productMapper) {
        this.masterTechScraperRepository = masterTechScraperRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductResponseDto> updateProducts() {
        List<ProductResponseDto> productList = new ArrayList<>();
        int page = 1;
        boolean hasNextPage = true;

        while (hasNextPage) {
            try {
                String url = this.buildPageUrl(page);
                logger.info("Procesando URL: " + url);

                Document doc = this.fetchDocument(url);
                Elements products = doc.select(CssSelectorsMessages.MASTERTECH_PRODUCT_LIST);

                if (products.isEmpty()) {
                    hasNextPage = false;
                    continue;
                }

                for (Element product : products) {
                    this.processProductElement(product, productList);
                }

                hasNextPage = this.hasNextPage(doc);
                page++;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error en la paginación: " + page, e);
                hasNextPage = false;
            }
        }
        return productList;
    }

    private String buildPageUrl(int page) {
        return page == 1 ? ScraperMessages.BASE_URL_MASTERTECH + "/" : ScraperMessages.BASE_URL_MASTERTECH + "/page/" + page + "/";
    }

    private Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(ScraperMessages.USER_AGENT)
                .timeout(10000)
                .get();
    }

    private void processProductElement(Element product, List<ProductResponseDto> productList) {
        try {
            String title = product.select(CssSelectorsMessages.MASTERTECH_TITLE).text();
            Double price = this.extractPrice(product);
            String imgUrl = product.select(CssSelectorsMessages.MASTERTECH_IMAGE).attr("src");
            String productUrl = product.select(CssSelectorsMessages.MASTERTECH_LINK).attr("href");

            Optional<Product> existingProduct = masterTechScraperRepository.findByProductUrl(productUrl);

            if (existingProduct.isPresent()) {
                this.updateExistingProduct(existingProduct.get(), title, imgUrl, price, productList);
            } else {
                this.insertNewProduct(title, imgUrl, productUrl, price, productList);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error procesando producto individual: " + e.getMessage(), e);
        }
    }

    private Double extractPrice(Element product) {
        Element priceElement = product.select(CssSelectorsMessages.MASTERTECH_PRICE).first();
        String priceText = priceElement != null ? priceElement.ownText().replaceAll("[^\\d]", "") : "0";
        return Double.valueOf(priceText);
    }

    private void updateExistingProduct(Product productToUpdate, String title, String imgUrl, Double price, List<ProductResponseDto> productList) {
        productToUpdate.setName(title);
        productToUpdate.setImageUrl(imgUrl);
        productToUpdate.setPage(ScraperMessages.PAGE_MASTERTECH);

        List<PriceHistory> priceHistory = productToUpdate.getPriceHistory();
        if (priceHistory.isEmpty() || !price.equals(priceHistory.get(priceHistory.size() - 1).getPrice())) {
            productToUpdate.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "PYG"));
        }

        this.masterTechScraperRepository.save(productToUpdate);
        productList.add(productMapper.toDto(productToUpdate));
    }

    private void insertNewProduct(String title, String imgUrl, String productUrl, Double price, List<ProductResponseDto> productList) {
        Product newProduct = new Product(title, imgUrl, productUrl, ScraperMessages.PAGE_MASTERTECH);
        newProduct.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "PYG"));
        masterTechScraperRepository.save(newProduct);
        productList.add(productMapper.toDto(newProduct));
    }

    private boolean hasNextPage(Document doc) {
        return doc.selectFirst(CssSelectorsMessages.MASTERTECH_NEXT_PAGE) != null;

    }

}
