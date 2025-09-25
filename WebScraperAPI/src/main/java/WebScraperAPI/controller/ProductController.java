package WebScraperAPI.controller;

import WebScraperAPI.dto.response.ProductResponseDto;
import WebScraperAPI.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> findAllProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "21") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) List<String> markets,
            @RequestParam(defaultValue = "") String search
    ) {
        Page<ProductResponseDto> products =
                productService.getAllProductsAndName(pageNumber, size, sortBy, sortDir, markets, search);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable String id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }


    @GetMapping("/offers")
    public ResponseEntity<Page<ProductResponseDto>> findOffers(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "21") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) List<String> markets,
            @RequestParam(defaultValue = "") String search
    ) {
        return ResponseEntity.ok(productService.getOffers(pageNumber, size, sortBy, sortDir, markets, search));
    }


}
