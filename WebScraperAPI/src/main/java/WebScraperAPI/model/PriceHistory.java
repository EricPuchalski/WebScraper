package WebScraperAPI.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PriceHistory {
    private Double price;
    private LocalDateTime date;
    private String currency;

}
