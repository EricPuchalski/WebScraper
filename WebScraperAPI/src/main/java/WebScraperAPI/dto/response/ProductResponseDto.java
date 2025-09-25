package WebScraperAPI.dto.response;


import lombok.*;

import java.util.List;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseDto {
    private String id;
    private String name;
    private List<PriceHistoryResponseDto> priceHistory;
    private String imageUrl;
    private String productUrl;
    private String discountPercentage;
    private Boolean hasPriceDropped;
    private String pageLogoUrl;
    private String page;
    private Double price;
    private String currency;
    private boolean active;
    private LocalDateTime lastActivationDate;
    private LocalDateTime lastDeactivationDate;

    // fecha de inserción
    private LocalDateTime date;
}
