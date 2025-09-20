package WebScraperAPI.service;

import WebScraperAPI.dto.request.ClientRequestDto;
import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.model.Client;

public interface ClientService {
    ClientResponseDto createClientForUser   (ClientRequestDto clientRequestDto);

    Client getByDniEntity(String dni);           // uso interno, devuelve la entidad
    ClientResponseDto getByDni(String dni);

}
