package WebScraper.service;

import WebScraper.model.Product;

import java.util.List;

public interface MasterTechScraperService {
    List<Product> updateProducts() throws InterruptedException;

    List<Product> findProducts(String name);

    List<Product> findAll();
}
