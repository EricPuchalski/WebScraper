package WebScraperAPI.service;

import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.model.Client;

public interface ClientService {
    ClientResponseDto createClientForUser   (String id, String dni, String name, String lastName);

    Client getByDniEntity(String dni);           // uso interno, devuelve la entidad
    ClientResponseDto getByDni(String dni);

}
