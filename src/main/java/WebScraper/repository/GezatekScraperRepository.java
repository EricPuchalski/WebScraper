package WebScraper.repository;

import WebScraper.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GezatekScraperRepository extends MongoRepository<Product, String> {
}
