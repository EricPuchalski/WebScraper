package WebScraperAPI.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "favorites")
@CompoundIndex(def = "{'clientId': 1, 'productId': 1}", unique = true)
public class Favorite {

    @Id
    private String id;

    private String clientId;
    private String productId;
}