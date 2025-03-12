package WebScraper.model;

import java.time.LocalDateTime;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
