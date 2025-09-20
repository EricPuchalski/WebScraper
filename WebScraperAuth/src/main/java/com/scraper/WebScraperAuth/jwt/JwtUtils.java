package com.scraper.WebScraperAuth.jwt;

import com.scraper.WebScraperAuth.service.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${scraper.app.jwtSecret}") // GUARDÁ una clave Base64 de >=64 bytes para HS512
    private String jwtSecretB64;

    @Value("${scraper.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    private SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretB64);
        return Keys.hmacShaKeyFor(keyBytes); // válido para HS512
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl u = (UserDetailsImpl) authentication.getPrincipal();
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(u.getUsername())
                .claim("id", u.getId())
                .claim("email", u.getEmail())
                .claim("roles", u.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
