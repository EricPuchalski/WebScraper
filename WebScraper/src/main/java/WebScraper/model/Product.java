package WebScraper.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
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
    private List<PriceHistory> priceHistory;
    private String imageUrl;
    private String pageLogoUrl;
    private String productUrl;
    @Indexed
    private Boolean hasPriceDropped;
    private String page;
    private Double price;
    private LocalDateTime date;
    private String currency;
    private boolean active;
    private LocalDateTime lastActivationDate;
    private LocalDateTime lastDeactivationDate;


}
