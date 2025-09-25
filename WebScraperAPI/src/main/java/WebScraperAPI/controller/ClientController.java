package WebScraperAPI.controller;

import WebScraperAPI.dto.request.ClientRequestDto;
import WebScraperAPI.dto.request.FavoriteRequestDto;
import WebScraperAPI.dto.response.ClientFavoritesResponseDto;
import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.dto.response.FavoriteResponseDto;
import WebScraperAPI.service.ClientService;
import WebScraperAPI.service.FavoriteService;
import WebScraperAPI.util.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/clients")
public class ClientController {

    private final FavoriteService favoriteService;
    private final ClientService clientService;
    private final JwtUtils jwtUtils;

    public ClientController(FavoriteService favoriteService, ClientService clientService, JwtUtils jwtUtils) {
        this.favoriteService = favoriteService;
        this.clientService = clientService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@RequestBody ClientRequestDto client) {
        return ResponseEntity.ok(clientService.createClientForUser(client));
    }

    @PutMapping("me/favorites/{productId}")
    public FavoriteResponseDto toggleFavorite(
            @PathVariable String productId,
            @RequestHeader String header) {
        String token = header.replace("Bearer ", "");
        String email = jwtUtils.getEmailFromJwt(token);
        return favoriteService.setFavorite(email, productId);
    }


    @GetMapping("/me/favorites")
    public ResponseEntity<List<ClientFavoritesResponseDto>> list(@RequestHeader String header) {
        String token = header.replace("Bearer ", "");
        String email = jwtUtils.getEmailFromJwt(token);

        return ResponseEntity.ok(favoriteService.listAll(email));
    }


}
