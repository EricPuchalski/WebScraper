package WebScraperNotification.com.event.consumer;

import WebScraperNotification.com.event.dto.PriceDropNotificationEvent;
import WebScraperNotification.com.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceDroppedService {


    private final ObjectMapper mapper;
    private final EmailService emailService;

    @KafkaListener(topics = "price-drop-notifications", groupId = "price-drop-notifier")
    public void on(String raw) {

        try {
            PriceDropNotificationEvent event = mapper.readValue(raw, PriceDropNotificationEvent.class);
            emailService.sendPriceDropNotification(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
