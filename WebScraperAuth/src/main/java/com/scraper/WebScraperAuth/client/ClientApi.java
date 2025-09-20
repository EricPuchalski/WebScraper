// src/main/java/com/scraper/WebScraperAuth/client/ClientApi.java
package com.scraper.WebScraperAuth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "client-ms",
        url = "localhost:8080/api/v1/clients"
)
public interface ClientApi {

    @PostMapping("")
    void createClient(CreateClientRequest req);
}
