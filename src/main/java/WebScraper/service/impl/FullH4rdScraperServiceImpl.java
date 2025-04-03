package WebScraper.service.impl;

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

@Service
public class FullH4rdScraperServiceImpl implements FullH4rdScraperService {
    private static final Logger logger = Logger.getLogger(FullH4rdScraperServiceImpl.class.getName());
    private static final String PAGE_NAME = "FullH4rd";
    private static final String BASE_URL = "https://fullh4rd.com.ar";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

    private final FullH4rdScraperRepository fullH4rdScraperRepository;
    private final ProductMapper productMapper;

    public FullH4rdScraperServiceImpl(FullH4rdScraperRepository fullH4rdScraperRepository, ProductMapper productMapper) {
        this.fullH4rdScraperRepository = fullH4rdScraperRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductResponseDto> updateProducts() {
        List<Product> productList = new ArrayList<>();
        List<String> categories = FullH4rdUtils.getAllCategoryUrls();

        for (String categoryUrl : categories) {
            try {
                int page = 1;
                boolean hasNextPage = true;

                while (hasNextPage) {
                    String url = buildPageUrl(categoryUrl, page);
                    logger.info("Procesando URL: " + url);

                    Document doc = fetchDocument(url);
                    Elements products = doc.select(".product-list");

                    if (products.isEmpty()) {
                        hasNextPage = false;
                        continue;
                    }

                    for (Element product : products) {
                        this.processProductElement(product, productList);
                    }

                    hasNextPage = this.hasNextPage(doc);
                    page++;
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error en URL " + categoryUrl, e);
            }
        }

        return this.convertToDto(productList);
    }

    private String buildPageUrl(String categoryUrl, int page) {
        return categoryUrl.replaceAll("/\\d+$", "/" + page);
    }

    private Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(10000)
                .get();
    }

    private void processProductElement(Element product, List<Product> productList) {
        try {
            String title = product.select(".info > h3").text();
            Double price = this.extractPrice(product);
            String imageUrl = this.buildFullUrl(product.select("a > div > img").attr("src"));
            String productUrl = this.buildFullUrl(product.select("a").attr("href"));

            Optional<Product> existingProduct = fullH4rdScraperRepository.findByProductUrl(productUrl);

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
        String priceText = product.select(".price").first().ownText();
        if (priceText.contains(",")) {
            priceText = priceText.split(",")[0];
        }
        return Double.valueOf(priceText.replaceAll("[^\\d]", ""));
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

        fullH4rdScraperRepository.save(productToUpdate);
        productList.add(productToUpdate);
    }

    private void insertNewProduct(String title, String imageUrl, String productUrl, Double price, List<Product> productList) {
        Product newProduct = new Product(title, imageUrl, productUrl, PAGE_NAME);
        newProduct.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "ARS"));
        fullH4rdScraperRepository.save(newProduct);
        productList.add(newProduct);
    }

    private boolean hasNextPage(Document doc) {
        return doc.selectFirst("a[rel=next]") != null;
    }

    private List<ProductResponseDto> convertToDto(List<Product> productList) {
        return productList.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> findProducts(String name) {
        return fullH4rdScraperRepository.findAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase())
                        && PAGE_NAME.equalsIgnoreCase(product.getPage()))
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> findAll() {
        return fullH4rdScraperRepository.findAll().stream()
                .filter(product -> PAGE_NAME.equalsIgnoreCase(product.getPage()))
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<ProductResponseDto> findProducts(String name, int page, int size) {
        List<Product> allProducts = fullH4rdScraperRepository.findByPage(PAGE_NAME);
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

        return new PageImpl<>(this.convertToDto(pageContent), pageable, filteredProducts.size());
    }

    @Override
    public Page<ProductResponseDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = fullH4rdScraperRepository.findByPage(PAGE_NAME, pageable);
        return new PageImpl<>(this.convertToDto(productPage.getContent()), pageable, productPage.getTotalElements());
    }
}
