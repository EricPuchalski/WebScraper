package WebScraperAPI.controller;

import WebScraperAPI.dto.request.ClientRequestDto;
import WebScraperAPI.dto.request.FavoriteRequestDto;
import WebScraperAPI.dto.response.ClientFavoritesResponseDto;
import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.dto.response.FavoriteResponseDto;
import WebScraperAPI.service.ClientService;
import WebScraperAPI.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("api/v1/clients")
public class ClientController {

    private final FavoriteService favoriteService;
    private final ClientService clientService;

    public ClientController(FavoriteService favoriteService, ClientService clientService) {
        this.favoriteService = favoriteService;
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@RequestBody ClientRequestDto client) {
        return ResponseEntity.ok(clientService.createClientForUser(client));
    }

    @PutMapping("me/favorites/{productId}")
    public FavoriteResponseDto toggleFavorite(
            @PathVariable String productId,
            @AuthenticationPrincipal Jwt jwt) throws AccessDeniedException {
        return favoriteService.setFavorite(extractEmail(jwt), productId);
    }


    @GetMapping("/me/favorites")
    public ResponseEntity<List<ClientFavoritesResponseDto>> list(@AuthenticationPrincipal Jwt jwt) throws AccessDeniedException {
        return ResponseEntity.ok(favoriteService.listAll(extractEmail(jwt)));
    }
    private String extractEmail(Jwt jwt) throws AccessDeniedException {
        if (jwt == null) {
            throw new AccessDeniedException("Missing authentication token");
        }
        return jwt.getClaim("email");
    }

}
