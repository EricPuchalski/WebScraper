package WebScraper.controller;

import WebScraper.model.Product;
import WebScraper.service.GezatekScraperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scraper/gezatek")
public class GezatekScraperController {
    private final GezatekScraperService gezatekScraperService;

    public GezatekScraperController(GezatekScraperService gezatekScraperService) {
        this.gezatekScraperService = gezatekScraperService;
    }

    @PostMapping()
    public ResponseEntity<List<Product>> updateProducts() {
        gezatekScraperService.updateProducts();
        return ResponseEntity.ok().body(null);
    }


}
