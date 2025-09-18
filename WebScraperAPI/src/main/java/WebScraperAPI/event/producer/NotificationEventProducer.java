package WebScraperAPI.event.producer;

import WebScraperAPI.event.dto.NotificationRequestEvent;
import WebScraperAPI.event.dto.PriceDropEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class NotificationEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendNotificationRequest(PriceDropEvent priceEvent, String userId) {
        try {
            NotificationRequestEvent notificationEvent = new NotificationRequestEvent(
                UUID.randomUUID().toString(),
                userId,
                priceEvent.productName(),
                priceEvent.productUrl(),
                priceEvent.oldPrice(),
                priceEvent.newPrice()
            );

            kafkaTemplate.send("notification-requests", notificationEvent);
            
            log.debug("Notification request sent for user: {} and product: {}", 
                     userId, priceEvent.productName());
                     
        } catch (Exception e) {
            log.error("Failed to send notification request for user: {}", userId, e);
        }
    }
}