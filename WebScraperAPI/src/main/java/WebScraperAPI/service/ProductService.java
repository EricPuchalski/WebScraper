package WebScraperAPI.service;

import WebScraperAPI.dto.response.ProductResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Page<ProductResponseDto> getAllProductsAndName(
            int pageNumber, int size, String sortBy, String sortDir, List<String> markets, String search
    );

    Page<ProductResponseDto> getOffers(
            int pageNumber, int size, String sortBy, String sortDir, List<String> markets, String search
    );

    ProductResponseDto getProductById(String id);
}