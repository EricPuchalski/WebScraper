package WebScraper.controller;

import WebScraper.model.Product;
import WebScraper.service.GezatekScraperService;
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
    public ResponseEntity<List<Product>> updateProducts() {
        gezatekScraperService.updateProducts();
        return ResponseEntity.ok().body(null);
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll(){
        List<Product> products = gezatekScraperService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/")
    public ResponseEntity<List<Product>> findProducts(@RequestParam String name){
        List<Product> products = gezatekScraperService.findProducts(name);
        if(products.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }

}
