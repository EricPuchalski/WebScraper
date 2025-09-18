package WebScraperAPI.event.consumer;

import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.event.dto.PriceDropEvent;
import WebScraperAPI.event.producer.NotificationEventProducer;
import WebScraperAPI.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PriceDropEventConsumer {

    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private NotificationEventProducer notificationProducer;

    @KafkaListener(topics = "price-drop-events")
    public void handlePriceDropEvent(PriceDropEvent event) {
        log.info("Processing price drop event for product: {}", event.productId());
        
        try {
            // Buscar usuarios que tienen este producto en favoritos
            List<ClientResponseDto> interestedUsers = favoriteService.listClientsWhoFavedProduct(event.productId());
            
            log.info("Found {} interested users for product: {}", 
                    interestedUsers.size(), event.productId());
            
            // Enviar evento de notificación por cada usuario
            interestedUsers.forEach(user -> {
                notificationProducer.sendNotificationRequest(
                    event, user.getId()
                );
            });
            
        } catch (Exception e) {
            log.error("Error processing price drop event: {}", event.eventId(), e);
            // Aquí podrías implementar retry logic o DLQ
        }
    }
}