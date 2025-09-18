// package WebScraper.kafka;
package WebScraper.event;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record PriceDropEvent(
        String eventId,
        String productId,
        String name,
        double oldPrice,
        double newPrice,
        long ts
) {}
