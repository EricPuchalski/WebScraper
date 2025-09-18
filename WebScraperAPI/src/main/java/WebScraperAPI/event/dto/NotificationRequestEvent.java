package WebScraperAPI.event.dto;

public record NotificationRequestEvent(
    String eventId,
    String userId,
    String productName,
    String productUrl,
    double oldPrice,
    double newPrice
) {}