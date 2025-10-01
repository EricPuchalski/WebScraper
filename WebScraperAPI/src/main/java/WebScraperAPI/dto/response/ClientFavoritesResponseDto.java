package WebScraperAPI.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ClientFavoritesResponseDto {
    private List<ProductSummaryResponseDto> products;
}
