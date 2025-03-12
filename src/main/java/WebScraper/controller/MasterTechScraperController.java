package WebScraper.controller;

import WebScraper.model.Product;
import WebScraper.service.MasterTechScraperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scraper/master-tech")
public class MasterTechScraperController {
    private final MasterTechScraperService masterTechScraperService;

    public MasterTechScraperController(MasterTechScraperService masterTechScraperService) {
        this.masterTechScraperService = masterTechScraperService;
    }

    @PostMapping()
    public ResponseEntity<List<Product>> updateProducts() throws InterruptedException {
        masterTechScraperService.updateProducts();
        return ResponseEntity.ok().body(null);
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll(){
        List<Product> products = masterTechScraperService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/")
    public ResponseEntity<List<Product>> findProducts(@RequestParam String name){
        List<Product> products = masterTechScraperService.findProducts(name);
        if(products.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }

}
