package WebScraperAPI.controller;

import WebScraperAPI.dto.ProductResponseDto;
import WebScraperAPI.model.Product;
import WebScraperAPI.service.ProductService;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> findAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size,
            @RequestParam(defaultValue = "") String market,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "name") String sortBy) {
        Page<ProductResponseDto> products = productService.getAllProductsAndName(page, size, sortBy, market, search);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable String id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }


}
