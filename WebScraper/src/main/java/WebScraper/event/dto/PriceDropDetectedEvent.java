package WebScraper.event.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PriceDropDetectedEvent(
        String eventId,
        String productId,
        Instant detectedAt
) {
}