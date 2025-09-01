package WebScraper.repository;

import WebScraper.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface ScraperRepository extends MongoRepository<Product, String> {
    Optional<Product> findByProductUrl(String productUrl);
    Page<Product> findByPage(String pageName, Pageable pageable);
    List<Product> findByPage(String pageName);
}
