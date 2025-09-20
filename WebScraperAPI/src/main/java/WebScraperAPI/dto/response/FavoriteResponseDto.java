package WebScraperAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FavoriteResponseDto {
    private String productId;
    private String clientDni;
    private boolean favorite;
}