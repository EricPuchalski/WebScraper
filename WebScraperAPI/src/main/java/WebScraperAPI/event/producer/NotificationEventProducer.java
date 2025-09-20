package WebScraperAPI.event.producer;

import WebScraperAPI.event.dto.PriceDropNotificationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public NotificationEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendNotificationRequest(PriceDropNotificationEvent notificationEvent) {
        try {
            String json = objectMapper.writeValueAsString(notificationEvent);
            kafkaTemplate.send("price-drop-notifications", json);
            log.debug("Notification request sent for client: {} {}", notificationEvent.clientName(), notificationEvent.clientLastname());
            
            log.debug("Notification request sent for user: {} and product: {}", 
                     notificationEvent.clientMail(), notificationEvent.productName());
                     
        } catch (Exception e) {
            log.error("Failed to send notification request for user: {}", notificationEvent.clientMail(), e);
        }
    }
}