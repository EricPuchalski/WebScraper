package WebScraperAPI.controller;

import WebScraperAPI.dto.request.FavoriteRequestDto;
import WebScraperAPI.dto.response.FavoriteResponseDto;
import WebScraperAPI.security.security.service.UserDetailsImpl;
import WebScraperAPI.service.FavoriteService;
import io.jsonwebtoken.Jwt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/clients")
public class ClientController {

    private final FavoriteService favoriteService;

    public ClientController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PutMapping("/me/favorites/{productId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<FavoriteResponseDto> setFavorite(
            @PathVariable String productId,
            @RequestBody FavoriteRequestDto req,
            @AuthenticationPrincipal UserDetailsImpl principal
    ) {
        String dni = principal.getDni();
        FavoriteResponseDto response = favoriteService.setFavorite(dni, productId, req.isFavorite());
        return ResponseEntity.ok(response);
    }

    @GetMapping("me/favorites")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<FavoriteResponseDto>> list(
            @AuthenticationPrincipal UserDetailsImpl principal
    ) {
        return ResponseEntity.ok(favoriteService.listAll(principal.getDni()));
    }

}
