package WebScraper.service;

import WebScraper.dto.ProductResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BaseScraperService {

    List<ProductResponseDto> updateProducts();

}
