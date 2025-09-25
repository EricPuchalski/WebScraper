package WebScraperAPI.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private String pageLogoUrl;
    private List<PriceHistory> priceHistory;
    private String imageUrl;
    private Boolean hasPriceDropped;
    private String productUrl;
    private String page;
    private Double price;
    private LocalDateTime date;
    private String currency;
    private boolean active;
    private LocalDateTime lastActivationDate;
    private LocalDateTime lastDeactivationDate;


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

}
