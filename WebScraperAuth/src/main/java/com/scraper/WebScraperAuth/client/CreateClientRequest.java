// src/main/java/com/scraper/WebScraperAuth/client/dto/CreateClientRequest.java
package com.scraper.WebScraperAuth.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class CreateClientRequest {
    private String userId;
    private String dni;
    private String name;
    private String lastName;
}
