package WebScraperAPI.dto.response;

import java.time.LocalDateTime;

public record ClientResponseDto(
        String id,
        String dni,
        String name,
        String lastName,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}