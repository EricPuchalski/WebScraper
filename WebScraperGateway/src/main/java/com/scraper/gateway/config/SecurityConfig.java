package com.scraper.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .authorizeExchange(ex -> ex
        .pathMatchers("/api/v1/clients").permitAll()
              .pathMatchers("api/v1/scraper/**").permitAll()
              .pathMatchers("api/v1/products/**").permitAll()
              .pathMatchers("api/v1/favorites/**").permitAll()
              .pathMatchers("api/v1/clients/me/**").hasRole("ROLE_CLIENT")
              .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
              .pathMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
              .anyExchange().authenticated()
      );
    return http.build();
  }
}