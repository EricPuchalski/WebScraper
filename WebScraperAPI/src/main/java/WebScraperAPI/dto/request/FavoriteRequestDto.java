package WebScraperAPI.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FavoriteRequestDto(
        @NotBlank
        String productId
) {}
