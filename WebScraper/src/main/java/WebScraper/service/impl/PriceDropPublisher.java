package WebScraper.service.impl;

import WebScraper.event.PriceDropEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceDropPublisher {
    private final KafkaTemplate<String, PriceDropEvent> kafka;

    public void publish(PriceDropEvent e) {
        // clave = productId para que todos los eventos del mismo producto vayan a la misma partición
        kafka.send("price-drop", e.productId(), e);
    }
}