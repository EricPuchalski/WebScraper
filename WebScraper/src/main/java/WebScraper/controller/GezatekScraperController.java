package WebScraper.controller;

import WebScraper.dto.ProductResponseDto;
import WebScraper.event.dto.PriceDropDetectedEvent;
import WebScraper.event.producer.PriceDropPublisher;
import WebScraper.model.Product;
import WebScraper.repository.GezatekScraperRepository;
import WebScraper.service.GezatekScraperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/scraper/gezatek")
public class GezatekScraperController {
    private final GezatekScraperService gezatekScraperService;
    private final PriceDropPublisher priceDropPublisher;
    private final GezatekScraperRepository gezatekScraperRepository;

    public GezatekScraperController(GezatekScraperService gezatekScraperService, PriceDropPublisher priceDropPublisher, GezatekScraperRepository gezatekScraperRepository) {
        this.gezatekScraperService = gezatekScraperService;
        this.priceDropPublisher = priceDropPublisher;
        this.gezatekScraperRepository = gezatekScraperRepository;
    }

    @PostMapping()
    public ResponseEntity<List<ProductResponseDto>> updateProducts() {
        gezatekScraperService.updateProducts();
        return ResponseEntity.ok().body(null);
    }
    @PostMapping("/simulate-price-drop/{productId}")
    public ResponseEntity<String> simulatePriceDrop(@PathVariable String productId) {
        try {
            Optional<Product> productOpt = gezatekScraperRepository.findById(productId);

            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Producto no encontrado");
            }

            Product product = productOpt.get();

            log.info("Simulando bajada de precio para producto: {} ({})",
                    product.getName(), productId);

            // Publicar evento de bajada de precio
            priceDropPublisher.publish(PriceDropDetectedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .productId(product.getId())
                    .detectedAt(Instant.now().atZone(ZoneId.systemDefault()).toInstant())
                    .build());

            return ResponseEntity.ok("Evento de bajada de precio simulado correctamente para: " + product.getName());

        } catch (Exception e) {
            log.error("Error simulando bajada de precio: ", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
