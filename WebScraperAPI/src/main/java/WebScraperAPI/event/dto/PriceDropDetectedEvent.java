package WebScraperAPI.event.dto;

import java.time.Instant;

public record PriceDropDetectedEvent(
    String eventId,
    String productId,
    Instant detectedAt
) {}