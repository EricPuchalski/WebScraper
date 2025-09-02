package WebScraper.service.impl;

import WebScraper.constant.CssSelectorsMessages;
import WebScraper.constant.ScraperMessages;
import WebScraper.dto.ProductResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import java.util.Optional;

@Service
public class GezatekScraperServiceImpl implements GezatekScraperService {
    private static final Logger logger = Logger.getLogger(GezatekScraperServiceImpl.class.getName());

    private final GezatekScraperRepository gezatekScraperRepository;
    private final ProductMapper productMapper;

    public GezatekScraperServiceImpl(GezatekScraperRepository gezatekScraperRepository, ProductMapper productMapper) {
        this.gezatekScraperRepository = gezatekScraperRepository;
        this.productMapper = productMapper;
}

    @Override
    public List<ProductResponseDto> updateProducts() {
        List<Product> productList = new ArrayList<>();
        List<String> categories = GezatekUtils.getAllCategoryUrls();

        for (String url : categories) {
            try {
                Document doc = fetchDocument(url);
                Elements products = doc.select(CssSelectorsMessages.GEZATEK_PRODUCT_LIST);

                for (Element product : products) {
                    processProductElement(product, productList);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error en URL " + url, e);
            }
        }

        return convertToDto(productList);
    }

    private Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(ScraperMessages.USER_AGENT)
                .timeout(10000)
                .get();
    }

    private void processProductElement(Element product, List<Product> productList) {
        try {
            String title = product.select(CssSelectorsMessages.GEZATEK_TITLE).text();
            Double price = extractPrice(product);
            String imageUrl = product.select(CssSelectorsMessages.GEZATEK_IMAGE).attr("src");
            String productUrl = buildFullUrl(product.select(CssSelectorsMessages.GEZATEK_LINK).attr("href"));

            Optional<Product> existingProduct = gezatekScraperRepository.findByProductUrl(productUrl);

            if (existingProduct.isPresent()) {
                updateExistingProduct(existingProduct.get(), title, imageUrl, price, productList);
            } else {
                insertNewProduct(title, imageUrl, productUrl, price, productList);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error procesando producto individual: " + e.getMessage(), e);
        }
    }

    private Double extractPrice(Element product) {
        String priceText = product.select(CssSelectorsMessages.GEZATEK_PRICE).text().substring(12);
        return Double.valueOf(priceText);
    }

    private String buildFullUrl(String relativeUrl) {
        return ScraperMessages.BASE_URL_GEZATEK + (relativeUrl.startsWith("/") ? "" : "/") + relativeUrl;
    }

    private void updateExistingProduct(Product productToUpdate, String title, String imageUrl, Double price, List<Product> productList) {
        productToUpdate.setName(title);
        productToUpdate.setImageUrl(imageUrl);
        productToUpdate.setPage(ScraperMessages.PAGE_GEZATEK);

        List<PriceHistory> priceHistory = productToUpdate.getPriceHistory();
        if (priceHistory.isEmpty() || !price.equals(priceHistory.get(priceHistory.size() - 1).getPrice())) {
            productToUpdate.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "ARS"));
        }

        gezatekScraperRepository.save(productToUpdate);
        productList.add(productToUpdate);
    }

    private void insertNewProduct(String title, String imageUrl, String productUrl, Double price, List<Product> productList) {
        Product newProduct = new Product(title, imageUrl, productUrl, ScraperMessages.PAGE_GEZATEK);
        newProduct.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "ARS"));
        gezatekScraperRepository.save(newProduct);
        productList.add(newProduct);
    }

    private List<ProductResponseDto> convertToDto(List<Product> productList) {
        return productList.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

}
