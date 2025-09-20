package WebScraperAPI.repository;

import WebScraperAPI.model.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends MongoRepository<Client, String> {
    boolean existsByUserId(String id);

   Optional<Client> findByDni(String dni);
}
