package WebScraperAPI.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClientResponseDto {
    private String id;
    private String userId;
    private String dni;
    private String email;
    private String name;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}