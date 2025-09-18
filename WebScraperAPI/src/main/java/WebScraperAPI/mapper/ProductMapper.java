package WebScraperAPI.mapper;

import WebScraperAPI.dto.response.ProductResponseDto;
import WebScraperAPI.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    @Autowired
    private PriceHistoryMapper priceHistoryMapper;

    public ProductResponseDto toDto(Product product) {
        if (product == null) return null;

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .productUrl(product.getProductUrl())
                .page(product.getPage())
                .priceHistory(
                        product.getPriceHistory() == null
                                ? Collections.emptyList()
                                : product.getPriceHistory()
                                .stream()
                                .map(priceHistoryMapper::toDto)
                                .collect(Collectors.toList())
                )
                .price(product.getPrice())                   // ← último precio
                .active(product.isActive())
                .lastActivationDate(product.getLastActivationDate())
                .lastDeactivationDate(product.getLastDeactivationDate())
                .date(product.getDate())
                .build();
    }

    public Product toEntity(ProductResponseDto dto) {
        if (dto == null) return null;

        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .imageUrl(dto.getImageUrl())
                .productUrl(dto.getProductUrl())
                .page(dto.getPage())
                .priceHistory(
                        dto.getPriceHistory() == null
                                ? Collections.emptyList()
                                : dto.getPriceHistory()
                                .stream()
                                .map(priceHistoryMapper::toEntity)
                                .collect(Collectors.toList())
                )
                .price(dto.getPrice())                       // ← último precio
                .active(dto.isActive())
                .lastActivationDate(dto.getLastActivationDate())
                .lastDeactivationDate(dto.getLastDeactivationDate())
                .date(dto.getDate())
                .build();
    }
}
