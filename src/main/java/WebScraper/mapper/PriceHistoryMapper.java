package WebScraper.mapper;

import WebScraper.dto.PriceHistoryResponseDto;
import WebScraper.model.PriceHistory;
import org.springframework.stereotype.Service;

@Service
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
