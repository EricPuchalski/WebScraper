package WebScraperAPI.mapper;

import WebScraperAPI.dto.response.ProductResponseDto;
import WebScraperAPI.dto.response.ProductSummaryResponseDto;
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

        ProductResponseDto dto = ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .productUrl(product.getProductUrl())
                .page(product.getPage())
                .pageLogoUrl(product.getPageLogoUrl())
                .priceHistory(
                        product.getPriceHistory() == null
                                ? Collections.emptyList()
                                : product.getPriceHistory()
                                .stream()
                                .map(priceHistoryMapper::toDto)
                                .collect(Collectors.toList())
                )
                .price(product.getPrice())
                .hasPriceDropped(product.getHasPriceDropped())
                .active(product.isActive())
                .lastActivationDate(product.getLastActivationDate())
                .lastDeactivationDate(product.getLastDeactivationDate())
                .date(product.getDate())
                .build();

        // Calculate discount if product has at least 2 price entries
        if (product.getHasPriceDropped()) {
            double previousPrice = product.getPriceHistory().get(product.getPriceHistory().size() - 2).getPrice();
            double currentPrice = product.getPriceHistory().get(product.getPriceHistory().size() - 1).getPrice();

            double diffPercentage = ((previousPrice - currentPrice) / previousPrice) * 100;
            dto.setDiscountPercentage(String.format("%.0f%%", diffPercentage));
        } else {
            dto.setDiscountPercentage("0%");
        }
        return dto;

    }


    public Product toEntity(ProductResponseDto dto) {
        if (dto == null) return null;

        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .imageUrl(dto.getImageUrl())
                .productUrl(dto.getProductUrl())
                .page(dto.getPage())
                .pageLogoUrl(dto.getPageLogoUrl())
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

    public ProductSummaryResponseDto toSummary(Product product){
        if (product == null) return null;

        return ProductSummaryResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .productUrl(product.getProductUrl())
                .page(product.getPage())
                .pageLogoUrl(product.getPageLogoUrl())
                .price(product.getPrice())
                .hasPriceDropped(product.getHasPriceDropped())
                .discountPercentage(product.getHasPriceDropped() ? product.getPriceHistory().size() >= 2 ?
                        String.format("%.0f%%", ((product.getPriceHistory().get(product.getPriceHistory().size() - 2).getPrice() - product.getPriceHistory().get(product.getPriceHistory().size() - 1).getPrice()) / product.getPriceHistory().get(product.getPriceHistory().size() - 2).getPrice()) * 100) : "0%" : "0%")
                .build();
    }
}
