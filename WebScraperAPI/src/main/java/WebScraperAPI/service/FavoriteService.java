package WebScraperAPI.service;

import WebScraperAPI.dto.request.FavoriteRequestDto;
import WebScraperAPI.dto.response.ClientFavoritesResponseDto;
import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.dto.response.FavoriteResponseDto;

import java.util.List;

public interface FavoriteService {
    FavoriteResponseDto setFavorite(String email, String productId);
    List<ClientFavoritesResponseDto> listAll(String username);

    List<ClientResponseDto> listClientsWhoFavedProduct(String productId);

}
