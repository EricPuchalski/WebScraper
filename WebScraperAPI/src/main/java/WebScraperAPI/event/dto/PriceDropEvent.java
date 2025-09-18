package WebScraperAPI.event.dto;

public record PriceDropEvent(
    String eventId,
    String productId,
    String productName,
    String productPage,
    String productUrl,
    double oldPrice,
    double newPrice
) {}

