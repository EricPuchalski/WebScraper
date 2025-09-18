package WebScraperAPI.service.impl;

import WebScraperAPI.dto.request.ClientRequestDto;
import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.mapper.ClientMapper;
import WebScraperAPI.model.Client;
import WebScraperAPI.repository.ClientRepository;
import WebScraperAPI.service.ClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientMapper mapper;
    private final ClientRepository clientRepository;

    public ClientServiceImpl( ClientMapper mapper, ClientRepository clientRepository) {
        this.mapper = mapper;
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public ClientResponseDto createClientForUser(ClientRequestDto request) {

        Client entity = Client.builder()
                .userId(request.getUserId())
                .dni(request.getDni())
                .name(request.getName())
                .lastName(request.getLastName())
                .build();

        Client saved = clientRepository.save(entity);
        return mapper.toResponse(saved);
    }


    @Override
    public Client getByDniEntity(String dni) {
        return clientRepository.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with dni: " + dni));
    }

    @Override
    public ClientResponseDto getByDni(String dni) {
        Client client = getByDniEntity(dni);
        return mapper.toResponse(client);
    }


}
