package WebScraper.model;

import java.time.LocalDateTime;

public class PriceHistory {
    private Double price;
    private LocalDateTime date;

    public PriceHistory() {
    }

    public PriceHistory(Double price, LocalDateTime date) {
        this.price = price;
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
