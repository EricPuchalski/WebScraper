package WebScraper.controller;

import WebScraper.dto.ProductResponseDto;
import WebScraper.service.MasterTechScraperService;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<List<ProductResponseDto>> updateProducts() throws InterruptedException {
        masterTechScraperService.updateProducts();
        return ResponseEntity.ok().body(null);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size) {
        Page<ProductResponseDto> products = masterTechScraperService.findAll(page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> findProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size) {

        Page<ProductResponseDto> products = masterTechScraperService.findProducts(name, page, size);

        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(products);
    }
}
