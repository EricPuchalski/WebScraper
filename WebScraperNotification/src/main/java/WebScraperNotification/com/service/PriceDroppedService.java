package WebScraperNotification.com.service;

=import WebScraperNotification.com.event.PriceDropEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceDroppedService {


    private final ObjectMapper mapper;

    @KafkaListener(topics = "price-drop", groupId = "notifier-test")
    public void on(String raw) throws Exception {
        PriceDropEvent e = mapper.readValue(raw, PriceDropEvent.class);


        if (emails.isEmpty()) {
            log.info("No se encontraron emails para productId={} (eventId={})", e.productId(), e.eventId());
            return;
        }

        for (String email : emails) {
            log.info("El producto {} bajó de precio de {} a {}, se lo envió al correo {}",
                    e.productId(), e.oldPrice(), e.newPrice(), email);
        }
    }
}
