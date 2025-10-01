package WebScraperAPI.service.impl;

import WebScraperAPI.dto.response.*;
import WebScraperAPI.mapper.ClientMapper;
import WebScraperAPI.mapper.FavoriteMapper;
import WebScraperAPI.mapper.ProductMapper;
import WebScraperAPI.model.Client;
import WebScraperAPI.model.Favorite;
import WebScraperAPI.model.Product;
import WebScraperAPI.repository.ClientRepository;
import WebScraperAPI.repository.FavoriteRepository;
import WebScraperAPI.repository.ProductRepository;
import WebScraperAPI.service.ClientService;
import WebScraperAPI.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FavoriteServiceImpl implements FavoriteService {
    private final ClientService clientService;
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ProductMapper productMapper;

    public FavoriteServiceImpl(ClientService clientService, ProductRepository productRepository, FavoriteRepository favoriteRepository, FavoriteMapper favoriteMapper, ClientRepository clientRepository, ClientMapper clientMapper, ProductMapper productMapper) {
        this.clientService = clientService;
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.productMapper = productMapper;
    }

    @Transactional
    public FavoriteResponseDto setFavorite(String email, String productId) {
        Client client = clientService.getEntityByEmail(email);
        String clientId = client.getId();

        boolean currentlyFavorite = favoriteRepository.existsByClientIdAndProductId(clientId, productId);

        if (currentlyFavorite) {
            favoriteRepository.deleteByClientIdAndProductId(clientId, productId);
        } else {
            // No es favorito →
            favoriteRepository.save(new Favorite(null, clientId, productId));
        }

        return FavoriteResponseDto.builder()
                .productId(productId)
                .clientEmail(email)
                .favorite(!currentlyFavorite)
                .build();
    }

    @Override
    public List<ClientFavoritesResponseDto> listAll(String email) {
        // 1) Cliente por email
        Client client = clientService.getEntityByEmail(email);

        // 2) Todos los favoritos del cliente
        List<Favorite> favs = favoriteRepository.findByClientId(client.getId());
        if (favs.isEmpty()) return List.of();

        // 3) Extraer IDs de productos
        List<String> productIds = favs.stream()
                .map(Favorite::getProductId)
                .distinct()
                .toList();

        // 4) Buscar productos por sus IDs
        List<Product> products = productRepository.findAllById(productIds);

        // 5) Mapear a ProductResponseDto
        List<ProductSummaryResponseDto> productDtos = products.stream()
                .map(productMapper::toSummary) // depende de tu mapper
                .toList();

        // 6) Armar la respuesta
        ClientFavoritesResponseDto response = new ClientFavoritesResponseDto();
        response.setProducts(productDtos);

        return List.of(response);
    }


    @Override
    public List<ClientResponseDto> listClientsWhoFavedProduct(String productId) {
        // 1) Todos los favoritos para ese producto
        log.info("Listing clients who favorited product {}", productId);

        List<Favorite> favs = favoriteRepository.findByProductId(productId);

        log.info("Found {} favorites for product {}", favs.size(), productId);
        if (favs.isEmpty()) return List.of();

        // 2) Traer clientes por sus IDs en bloque
        List<String> clientIds = favs.stream()
                .map(Favorite::getClientId)
                .distinct()
                .toList();

        log.info("Found {} clients who favorited product {}", clientIds.size(), productId);

        List<Client> clientsById = new ArrayList<>(clientRepository.findAllById(clientIds));

        return clientsById
                .stream()
                .map(clientMapper::toResponse)
                .toList();

    }
}
