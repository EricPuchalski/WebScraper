package WebScraper.service;

import WebScraper.model.Product;

import java.util.List;

public interface GezatekScraperService {
    List<Product> updateProducts();

    List<Product> findProducts(String name);
}
