package WebScraperAPI.event.consumer;

import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.event.dto.PriceDropDetectedEvent;
import WebScraperAPI.event.dto.PriceDropNotificationEvent;
import WebScraperAPI.event.producer.NotificationEventProducer;
import WebScraperAPI.model.Client;
import WebScraperAPI.model.PriceHistory;
import WebScraperAPI.model.Product;
import WebScraperAPI.repository.ClientRepository;
import WebScraperAPI.repository.ProductRepository;
import WebScraperAPI.service.FavoriteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class PriceDropEventConsumer {

    private final FavoriteService favoriteService;
    private final NotificationEventProducer notificationProducer;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper;

    public PriceDropEventConsumer(FavoriteService favoriteService, NotificationEventProducer notificationProducer, ProductRepository productRepository, ClientRepository clientRepository, ObjectMapper objectMapper) {
        this.favoriteService = favoriteService;
        this.notificationProducer = notificationProducer;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "price-drop-detected")
    public void handlePriceDropEvent(String raw) {

        try {
            PriceDropDetectedEvent event =
                    objectMapper.readValue(raw, PriceDropDetectedEvent.class);
            // Buscar usuarios que tienen este producto en favoritos
            Product product = productRepository.findById(event.productId())
                    .orElseThrow(() -> new IllegalStateException("Product not found: " + event.productId()));

            PriceSnapshot snapshot = extractPriceSnapshot(product);

            List<ClientResponseDto> interestedUsers = favoriteService.listClientsWhoFavedProduct(event.productId());


            log.info("Found {} interested users for product: {}", 
                    interestedUsers.size(), event.productId());

            interestedUsers.stream()
                    .filter(user -> user.getId() != null && !user.getId().isBlank())
                    .forEach(user -> clientRepository.findById(user.getId())
                            .map(Client::getEmail)
                            .filter(email -> !email.isBlank())
                            .ifPresentOrElse(
                                    email -> sendNotification(event, product, snapshot, user, email),
                                    () -> log.warn("Skipping notification for client {} because email was not found", user.getId())
                            )
                    );

            
        } catch (Exception e) {
            log.error("Error processing price drop event: {}", raw);
            // Aquí podrías implementar retry logic o DLQ
        }
    }

    private void sendNotification(PriceDropDetectedEvent event,
                                  Product product,
                                  PriceSnapshot snapshot,
                                  ClientResponseDto client,
                                  String email) {
        PriceDropNotificationEvent notificationEvent = new PriceDropNotificationEvent(
                event.eventId(),
                event.productId(),
                product.getName(),
                product.getProductUrl(),
                product.getPage(),
                snapshot.oldPrice(),
                snapshot.newPrice(),
                client.getName(),
                client.getLastName(),
                email
        );

        notificationProducer.sendNotificationRequest(notificationEvent);
    }

    private PriceSnapshot extractPriceSnapshot(Product product) {
        List<PriceHistory> history = List.of();
        history = Optional.ofNullable(product.getPriceHistory()).orElse(List.of());

        List<PriceHistory> finalHistory = history;
        double latestPrice = Optional.ofNullable(product.getPrice())
                .orElseGet(() -> finalHistory.isEmpty() ? 0d : finalHistory.get(finalHistory.size() - 1).getPrice());

        double previousPrice = history.size() > 1
                ? history.get(history.size() - 2).getPrice()
                : latestPrice;

        return new PriceSnapshot(previousPrice, latestPrice);
    }

    private record PriceSnapshot(double oldPrice, double newPrice) {
    }
}