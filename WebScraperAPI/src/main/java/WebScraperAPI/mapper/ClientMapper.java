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
                .name(req.getName())
                .email(req.getEmail())
                .lastName(req.getLastName())
                .build();
    }

    public ClientResponseDto toResponse(Client c) {
        return ClientResponseDto.builder()
                .id(c.getId())
                .userId(c.getUserId())
                .name(c.getName())
                .lastName(c.getLastName())
                .email(c.getEmail())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
