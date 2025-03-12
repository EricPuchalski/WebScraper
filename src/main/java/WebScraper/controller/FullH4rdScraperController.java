package WebScraper.controller;

import WebScraper.model.Product;
import WebScraper.service.FullH4rdScraperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/scraper/fullh4rd")
public class FullH4rdScraperController {
    private final FullH4rdScraperService fullH4rdScraperService;

    public FullH4rdScraperController(FullH4rdScraperService fullH4rdScraperService) {
        this.fullH4rdScraperService = fullH4rdScraperService;
    }

    @PostMapping()
    public ResponseEntity<List<Product>> updateProducts() {
        fullH4rdScraperService.updateProducts();
        return ResponseEntity.ok().body(null);
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        List<Product> products = fullH4rdScraperService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/")
    public ResponseEntity<List<Product>> findProducts(@RequestParam String name) {
        List<Product> products = fullH4rdScraperService.findProducts(name);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }
}
