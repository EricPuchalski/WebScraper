package WebScraperAPI.service.impl;

import WebScraperAPI.dto.response.ClientResponseDto;
import WebScraperAPI.mapper.ClientMapper;
import WebScraperAPI.model.Client;
import WebScraperAPI.security.model.User;
import WebScraperAPI.repository.ClientRepository;
import WebScraperAPI.security.repository.UserRepository;
import WebScraperAPI.service.ClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceImpl implements ClientService {

    private final UserRepository userRepository;
    private final ClientMapper mapper;
    private final ClientRepository clientRepository;

    public ClientServiceImpl(UserRepository userRepository, ClientMapper mapper, ClientRepository clientRepository) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public ClientResponseDto createClientForUser(String id, String dni, String name, String lastName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (clientRepository.existsByUserId(user.getId())) {
            throw new IllegalStateException("Client profile already exists");
        }

        Client entity = Client.builder()
                .userId(user.getId())
                .dni(dni)
                .name(name)
                .lastName(lastName)
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
