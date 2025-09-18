package WebScraperNotification.com.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "favorites")
public class Favorite {
    @Id private String id;
    private String clientId;
    private String productId;
}
