package WebScraperAPI.security.repository;



import WebScraperAPI.security.model.ERole;
import WebScraperAPI.security.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
