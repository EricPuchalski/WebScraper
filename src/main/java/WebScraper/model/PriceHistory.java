package WebScraper.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class PriceHistory {
    private Double price;
    private LocalDateTime date;
    private String currency;

    public PriceHistory() {
    }

    public PriceHistory(Double price, LocalDateTime date, String currency) {
        this.price = price;
        this.date = date;
        this.currency = currency;
    }


}
