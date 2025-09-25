package WebScraperAPI.repository;

import WebScraperAPI.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    Page<Product> findByHasPriceDroppedTrue(Pageable pageable);
    Page<Product> findByHasPriceDroppedTrueAndPageIn(List<String> markets, Pageable pageable);
    Page<Product> findByHasPriceDroppedTrueAndNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByHasPriceDroppedTrueAndPageInAndNameContainingIgnoreCase(List<String> markets, String name, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // 👇 Nuevos para múltiples markets
    Page<Product> findByPageIn(Collection<String> pages, Pageable pageable);
    Page<Product> findByPageInAndNameContainingIgnoreCase(Collection<String> pages, String name, Pageable pageable);
}