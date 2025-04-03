package WebScraper.service.impl;

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
    private static final String PAGE_NAME = "Gezatek";
    private static final String BASE_URL = "https://www.gezatek.com.ar";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

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
                Elements products = doc.select(".product");

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
                .userAgent(USER_AGENT)
                .timeout(10000)
                .get();
    }

    private void processProductElement(Element product, List<Product> productList) {
        try {
            String title = product.select("h2").text();
            Double price = extractPrice(product);
            String imageUrl = product.select(".img-responsive").attr("src");
            String productUrl = buildFullUrl(product.select(".click").attr("href"));

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
        String priceText = product.select("div > h3").text().substring(12);
        return Double.valueOf(priceText);
    }

    private String buildFullUrl(String relativeUrl) {
        return BASE_URL + (relativeUrl.startsWith("/") ? "" : "/") + relativeUrl;
    }

    private void updateExistingProduct(Product productToUpdate, String title, String imageUrl, Double price, List<Product> productList) {
        productToUpdate.setName(title);
        productToUpdate.setImageUrl(imageUrl);
        productToUpdate.setPage(PAGE_NAME);

        List<PriceHistory> priceHistory = productToUpdate.getPriceHistory();
        if (priceHistory.isEmpty() || !price.equals(priceHistory.get(priceHistory.size() - 1).getPrice())) {
            productToUpdate.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "ARS"));
        }

        gezatekScraperRepository.save(productToUpdate);
        productList.add(productToUpdate);
    }

    private void insertNewProduct(String title, String imageUrl, String productUrl, Double price, List<Product> productList) {
        Product newProduct = new Product(title, imageUrl, productUrl, PAGE_NAME);
        newProduct.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "ARS"));
        gezatekScraperRepository.save(newProduct);
        productList.add(newProduct);
    }

    private List<ProductResponseDto> convertToDto(List<Product> productList) {
        return productList.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> findProducts(String name) {
        return gezatekScraperRepository.findAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase())
                        && PAGE_NAME.equalsIgnoreCase(product.getPage()))
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> findAll() {
        return gezatekScraperRepository.findAll().stream()
                .filter(product -> PAGE_NAME.equalsIgnoreCase(product.getPage()))
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<ProductResponseDto> findProducts(String name, int page, int size) {
        List<Product> allProducts = gezatekScraperRepository.findByPage(PAGE_NAME);
        String searchTerm = name.trim().toLowerCase();

        List<Product> filteredProducts = allProducts.stream()
                .filter(product -> product.getName() != null &&
                        product.getName().trim().toLowerCase().contains(searchTerm))
                .toList();

        Pageable pageable = PageRequest.of(page, size);
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), filteredProducts.size());

        List<Product> pageContent = (fromIndex > filteredProducts.size())
                ? List.of()
                : filteredProducts.subList(fromIndex, toIndex);

        return new PageImpl<>(convertToDto(pageContent), pageable, filteredProducts.size());
    }

    @Override
    public Page<ProductResponseDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = gezatekScraperRepository.findByPage(PAGE_NAME, pageable);
        return new PageImpl<>(convertToDto(productPage.getContent()), pageable, productPage.getTotalElements());
    }
}
