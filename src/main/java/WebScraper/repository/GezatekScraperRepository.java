package WebScraper.repository;

import WebScraper.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GezatekScraperRepository extends MongoRepository<Product, String> {

    Optional<Product> findByProductUrl(String productUrl);

    Page<Product> findByPage(String page, Pageable pageable);

    List<Product> findByPage(String page);

}
