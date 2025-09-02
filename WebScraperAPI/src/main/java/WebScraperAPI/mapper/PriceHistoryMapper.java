package WebScraperAPI.mapper;

import WebScraperAPI.dto.PriceHistoryResponseDto;
import WebScraperAPI.model.PriceHistory;
import org.springframework.stereotype.Component;

@Component
public class PriceHistoryMapper {

    public PriceHistoryResponseDto toDto(PriceHistory priceHistory){
        if (priceHistory == null) {
            return null;
        }

        return PriceHistoryResponseDto.builder()
                .price(priceHistory.getPrice())
                .date(priceHistory.getDate())
                .currency(priceHistory.getCurrency()).build();
    }

    public PriceHistory toEntity(PriceHistoryResponseDto priceHistoryDto){
        if (priceHistoryDto == null) {
            return null;
        }
        return PriceHistory.builder()
                .price(priceHistoryDto.getPrice())
                .date(priceHistoryDto.getDate())
                .currency(priceHistoryDto.getCurrency()).build();


    }
}
