package WebScraperAPI.util;

import WebScraperAPI.security.model.ERole;
import WebScraperAPI.security.model.Role;
import WebScraperAPI.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

  private final RoleRepository roleRepository;

  @Override
  public void run(String... args) {
    if (roleRepository.findByName(ERole.ROLE_CLIENT).isEmpty()) {
      roleRepository.save(new Role(ERole.ROLE_CLIENT));
    }
    if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
      roleRepository.save(new Role(ERole.ROLE_ADMIN));
    }
  }
}
