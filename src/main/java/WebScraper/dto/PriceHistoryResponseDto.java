package WebScraper.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceHistoryResponseDto {
    private Double price;
    private LocalDateTime date;
    private String currency;
}
