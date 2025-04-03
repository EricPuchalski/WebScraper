package WebScraper.controller;

import WebScraper.dto.ProductResponseDto;
import WebScraper.service.GezatekScraperService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scraper/gezatek")
public class GezatekScraperController {
    private final GezatekScraperService gezatekScraperService;

    public GezatekScraperController(GezatekScraperService gezatekScraperService) {
        this.gezatekScraperService = gezatekScraperService;
    }

    @PostMapping()
    public ResponseEntity<List<ProductResponseDto>> updateProducts() {
        gezatekScraperService.updateProducts();
        return ResponseEntity.ok().body(null);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size) {
        Page<ProductResponseDto> products = gezatekScraperService.findAll(page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> findProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size) {

        Page<ProductResponseDto> products = gezatekScraperService.findProducts(name, page, size);

        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(products);
    }

}
