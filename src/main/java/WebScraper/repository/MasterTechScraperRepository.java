package WebScraper.repository;

import WebScraper.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MasterTechScraperRepository extends MongoRepository<Product, String> {
    Optional<Product> findByProductUrl(String productUrl);

}
