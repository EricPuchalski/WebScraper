package com.scraper.WebScraperAuth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientRequestDto {
        @NotBlank
        private String userId;

        private String dni;

        @NotBlank
        private String name;

        @NotBlank
        private String lastName;
}