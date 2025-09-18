package WebScraperAPI.repository;

import WebScraperAPI.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Page<Product> findByPageIgnoreCaseAndNameContainingIgnoreCase(String page, String name, Pageable pageable);
    Page<Product> findByPageIgnoreCase(String page, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
