package WebScraperAPI.repository;

import WebScraperAPI.model.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    List<Favorite> findByClientId(String clientId);
    boolean existsByClientIdAndProductId(String clientId, String productId);
    void deleteByClientIdAndProductId(String clientId, String productId);
}