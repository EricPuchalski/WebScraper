package WebScraper.repository;

import WebScraper.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MasterTechScraperRepository extends MongoRepository<Product, String> {

    Optional<Product> findByProductUrl(String productUrl);

    Page<Product> findByPage(String page, Pageable pageable);

    List<Product> findByPage(String page);

}
