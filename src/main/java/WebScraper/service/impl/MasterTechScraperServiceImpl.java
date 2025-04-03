package WebScraper.service.impl;

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

@Service
public class MasterTechScraperServiceImpl implements MasterTechScraperService {
    private static final Logger logger = Logger.getLogger(MasterTechScraperServiceImpl.class.getName());
    private static final String PAGE_NAME = "MasterTech";
    private static final String BASE_URL = "https://www.mastertech.com.py/productos";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

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
                Elements products = doc.select(".product");

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
        return page == 1 ? BASE_URL + "/" : BASE_URL + "/page/" + page + "/";
    }

    private Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(10000)
                .get();
    }

    private void processProductElement(Element product, List<ProductResponseDto> productList) {
        try {
            String title = product.select(".ast-loop-product__link").text();
            Double price = this.extractPrice(product);
            String imgUrl = product.select("noscript > img").attr("src");
            String productUrl = product.select(".ast-loop-product__link").attr("href");

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
        Element priceElement = product.select(".price span bdi").first();
        String priceText = priceElement != null ? priceElement.ownText().replaceAll("[^\\d]", "") : "0";
        return Double.valueOf(priceText);
    }

    private void updateExistingProduct(Product productToUpdate, String title, String imgUrl, Double price, List<ProductResponseDto> productList) {
        productToUpdate.setName(title);
        productToUpdate.setImageUrl(imgUrl);
        productToUpdate.setPage(PAGE_NAME);

        List<PriceHistory> priceHistory = productToUpdate.getPriceHistory();
        if (priceHistory.isEmpty() || !price.equals(priceHistory.get(priceHistory.size() - 1).getPrice())) {
            productToUpdate.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "PYG"));
        }

        this.masterTechScraperRepository.save(productToUpdate);
        productList.add(productMapper.toDto(productToUpdate));
    }

    private void insertNewProduct(String title, String imgUrl, String productUrl, Double price, List<ProductResponseDto> productList) {
        Product newProduct = new Product(title, imgUrl, productUrl, PAGE_NAME);
        newProduct.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "PYG"));
        masterTechScraperRepository.save(newProduct);
        productList.add(productMapper.toDto(newProduct));
    }

    private boolean hasNextPage(Document doc) {
        return doc.selectFirst("a.next") != null;
    }

    @Override
    public List<ProductResponseDto> findProducts(String name) {
        return this.masterTechScraperRepository.findAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase())
                        && PAGE_NAME.equalsIgnoreCase(product.getPage()))
                .map(this.productMapper::toDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> findAll() {
        return this.masterTechScraperRepository.findAll().stream()
                .filter(product -> PAGE_NAME.equalsIgnoreCase(product.getPage()))
                .map(this.productMapper::toDto)
                .toList();
    }

    public Page<ProductResponseDto> findProducts(String name, int page, int size) {
        List<Product> allProducts = this.masterTechScraperRepository.findByPage(PAGE_NAME);
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

        return new PageImpl<>(pageContent.stream().map(this.productMapper::toDto).toList(), pageable, filteredProducts.size());
    }

    @Override
    public Page<ProductResponseDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = this.masterTechScraperRepository.findByPage(PAGE_NAME, pageable);
        return new PageImpl<>(productPage.getContent().stream().map(this.productMapper::toDto).toList(), pageable, productPage.getTotalElements());
    }
}
