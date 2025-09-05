package WebScraperAPI.mapper;

import WebScraperAPI.dto.request.ClientRequestDto;
import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public Client toEntity(ClientRequestDto dto) {
        if (dto == null) return null;
        Client client = new Client();
        client.setDni(dto.dni());
        client.setName(dto.name());
        client.setLastName(dto.lastName());
        client.setEmail(dto.email());
        return client;
    }

    public ClientResponseDto toResponseDto(Client client) {
        if (client == null) return null;
        return new ClientResponseDto(
                client.getId(),
                client.getDni(),
                client.getName(),
                client.getLastName(),
                client.getEmail(),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }
}
