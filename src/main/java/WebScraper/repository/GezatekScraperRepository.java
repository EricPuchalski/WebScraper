package WebScraper.repository;

import WebScraper.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GezatekScraperRepository extends MongoRepository<Product, String> {
    Optional<Product> findByProductUrl(String productUrl);
}
