package WebScraperAPI.controller;

import WebScraperAPI.dto.request.ClientRequestDto;
import WebScraperAPI.dto.request.FavoriteRequestDto;
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

    @PutMapping("/me/favorites/{productId}")
   public ResponseEntity<FavoriteResponseDto> setFavorite(
            @PathVariable String productId,
            @RequestBody FavoriteRequestDto req,
            @RequestHeader String header
    ) {
        String token = header.replace("Bearer ", "");
        String dni = jwtUtils.getDniFromJwt(token);

        FavoriteResponseDto response = favoriteService.setFavorite(dni, productId, req.isFavorite());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/favorites")
    public ResponseEntity<List<FavoriteResponseDto>> list(@RequestHeader String header) {
        String token = header.replace("Bearer ", "");
        String dni = jwtUtils.getDniFromJwt(token);

        return ResponseEntity.ok(favoriteService.listAll(dni));
    }


}
