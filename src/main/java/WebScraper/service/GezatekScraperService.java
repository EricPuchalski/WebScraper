package WebScraper.service;

import WebScraper.dto.ProductResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GezatekScraperService {
    List<ProductResponseDto> updateProducts();

    List<ProductResponseDto> findProducts(String name);

    List<ProductResponseDto> findAll();
    public Page<ProductResponseDto> findProducts(String name, int page, int size) ;

    public Page<ProductResponseDto> findAll(int page, int size);
}
