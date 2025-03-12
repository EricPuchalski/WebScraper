package WebScraper.service;

import WebScraper.model.Product;

import java.util.List;

public interface FullH4rdScraperService {
    List    <Product> updateProducts();
    List<Product> findProducts(String name);
    List<Product> findAll();
}
