package WebScraperAPI.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRequestDto(
        @NotBlank @Size(max = 30)
        String dni,

        @NotBlank @Size(max = 50)
        String name,

        @NotBlank @Size(max = 50)
        String lastName,

        @NotBlank @Email @Size(max = 100)
        String email
) {}
