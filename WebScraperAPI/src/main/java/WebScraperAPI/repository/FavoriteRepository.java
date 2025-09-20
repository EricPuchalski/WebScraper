package WebScraperAPI.repository;

import WebScraperAPI.model.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    List<Favorite> findByClientId(String clientId);
    void deleteByClientIdAndProductId(String clientId, String productId);
    List<Favorite> findByProductId(String productId);
}