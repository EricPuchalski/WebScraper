package WebScraperNotification.com.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestKafka {

    @KafkaListener(topics = "products-updates", groupId = "test-group")
    public void listen(String message) {
        System.out.println("⚡ Mensaje recibido: " + message);
    }
}
