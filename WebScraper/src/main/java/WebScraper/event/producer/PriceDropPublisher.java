package WebScraper.event.producer;

import WebScraper.event.dto.PriceDropDetectedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceDropPublisher {
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper objectMapper;

    public void publish(PriceDropDetectedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafka.send("price-drop-detected", event.productId(), json);
            log.info("Published price-drop-detected key={} json={}", event.productId(), json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize PriceDropDetectedEvent", e);
        }
    }
}