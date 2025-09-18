package WebScraper.config;

import WebScraper.event.PriceDropEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

@Configuration
public class TestConfig {

    @Bean
    CommandLineRunner sendTestPriceDrop(KafkaTemplate<String, PriceDropEvent> kafka) {
        return args -> {
            PriceDropEvent event = new PriceDropEvent(
                    UUID.randomUUID().toString(),
                    "P-TEST-1",
                    "Producto de Prueba",
                    1000.0,
                    800.0,
                    System.currentTimeMillis()
            );

            kafka.send("price-drop", event.productId(), event);
            System.out.println(">>> Evento de prueba enviado: " + event);
        };
    }
}
