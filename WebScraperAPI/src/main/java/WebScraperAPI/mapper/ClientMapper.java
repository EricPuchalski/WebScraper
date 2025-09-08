package WebScraperAPI.mapper;

import WebScraperAPI.dto.request.ClientRequestDto;
import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequestDto req) {
        return Client.builder()
                .userId(req.getUserId())
                .dni(req.getDni())
                .name(req.getName())
                .lastName(req.getLastName())
                .build();
    }

    public ClientResponseDto toResponse(Client c) {
        return ClientResponseDto.builder()
                .id(c.getId())
                .userId(c.getUserId())
                .dni(c.getDni())
                .name(c.getName())
                .lastName(c.getLastName())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
