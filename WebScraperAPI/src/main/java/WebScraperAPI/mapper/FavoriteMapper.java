package WebScraperAPI.mapper;

import WebScraperAPI.dto.response.FavoriteResponseDto;
import WebScraperAPI.model.Favorite;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    public Favorite toEntity(String clientId, String productId) {
        return Favorite.builder()
                .clientId(clientId)
                .productId(productId)
                .build();
    }

    public FavoriteResponseDto toResponseDto(Favorite favorite, String productName, String clientDni) {
        if (favorite == null) return null;
        return FavoriteResponseDto.builder()
                .productId(favorite.getProductId())
                .clientDni(clientDni)
                .favorite(true)
                .build();
    }
}
