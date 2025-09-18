package WebScraperAPI.controller;

import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<ClientResponseDto>> listByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(favoriteService.listClientsWhoFavedProduct(productId));
    }
}
