package WebScraperAPI.service;

import WebScraperAPI.dto.ProductResponseDto;
import org.springframework.data.domain.Page;

public interface ProductService {

    Page<ProductResponseDto> getAllProductsAndName(int page, int size, String sortBy, String market, String name);

    ProductResponseDto getProductById(String id);
}
