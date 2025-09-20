package WebScraperAPI.event.dto;

public record PriceDropNotificationEvent(
        String eventId,
        String productId,
        String productName,
        String productUrl,
        String productPage,
        double productOldPrice,
        double productNewPrice,
        String clientName,
        String clientLastname,
        String clientMail
) {}