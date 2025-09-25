package WebScraperAPI.mapper;

import WebScraperAPI.dto.response.FavoriteResponseDto;
import WebScraperAPI.model.Favorite;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    public FavoriteResponseDto toResponseDto(Favorite favorite, String productName, String clientDni) {
        if (favorite == null) return null;
        return FavoriteResponseDto.builder()
                .productId(favorite.getProductId())
                .clientEmail(clientDni)
                .favorite(true)
                .build();
    }
}
