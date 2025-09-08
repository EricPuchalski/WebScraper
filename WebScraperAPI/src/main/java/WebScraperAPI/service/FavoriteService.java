package WebScraperAPI.service;

import WebScraperAPI.dto.request.FavoriteRequestDto;
import WebScraperAPI.dto.response.FavoriteResponseDto;

import java.util.List;

public interface FavoriteService {
    FavoriteResponseDto setFavorite(String dni, String productId, boolean favorite);

    List<FavoriteResponseDto> listAll(String username);
}
