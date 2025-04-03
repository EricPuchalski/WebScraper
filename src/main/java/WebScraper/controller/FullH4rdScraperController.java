package WebScraper.controller;

import WebScraper.dto.ProductResponseDto;
import WebScraper.service.FullH4rdScraperService;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<List<ProductResponseDto>> updateProducts() {
        fullH4rdScraperService.updateProducts();
        return ResponseEntity.ok().body(null);
    }
    //TODO UNA FUNCION PARA TRAER LA CANTIDAD DE PRODUCTOS TOTALES

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size) {
        Page<ProductResponseDto> products = fullH4rdScraperService.findAll(page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> findProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size) {

        Page<ProductResponseDto> products = fullH4rdScraperService.findProducts(name, page, size);

        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(products);
    }
}
