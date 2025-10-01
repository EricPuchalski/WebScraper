package WebScraperAPI.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSummaryResponseDto {
    private String id;
    private String name;
    private String imageUrl;
    private String productUrl;
    private String discountPercentage;
    private Boolean hasPriceDropped;
    private String pageLogoUrl;
    private String page;
    private Double price;

}
