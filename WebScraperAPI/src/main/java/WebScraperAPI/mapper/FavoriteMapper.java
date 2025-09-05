package WebScraperAPI.mapper;


import WebScraperAPI.dto.request.FavoriteRequestDto;
import WebScraperAPI.dto.response.FavoriteResponseDto;
import WebScraperAPI.model.Favorite;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    public Favorite toEntity(FavoriteRequestDto dto, String clientId) {
        if (dto == null) return null;
        Favorite favorite = new Favorite();
        favorite.setClientId(clientId);
        favorite.setProductId(dto.productId());
        return favorite;
    }

    public FavoriteResponseDto toResponseDto(Favorite favorite) {
        if (favorite == null) return null;
        return new FavoriteResponseDto(
                favorite.getId(),
                favorite.getClientId(),
                favorite.getProductId()
        );
    }
}
