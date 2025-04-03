package WebScraper.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private List<PriceHistory> priceHistory; // Historial de precios
    private String imageUrl;
    private String productUrl;
    private String page;
    private Double price;
    private LocalDateTime date;
    private String currency;

    public Product(String id, String name, List<PriceHistory> priceHistory, String imageUrl, String productUrl, String page, Double price, LocalDateTime date, String currency) {
        this.id = id;
        this.name = name;
        this.priceHistory = new ArrayList<>();
        this.imageUrl = imageUrl;
        this.productUrl = productUrl;
        this.page = page;
        this.price = price;
        this.date = date;
        this.currency = currency;
    }

    public Product(String title, String imageUrl, String productUrl, String page) {
        this.name = title;
        this.imageUrl = imageUrl;
        this.productUrl = productUrl;
        this.page = page;
        this.priceHistory = new ArrayList<>();
    }

    public Product() {
        this.priceHistory = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PriceHistory> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<PriceHistory> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
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

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
