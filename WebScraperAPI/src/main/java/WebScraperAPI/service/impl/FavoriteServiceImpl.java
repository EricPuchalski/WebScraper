package WebScraperAPI.service.impl;

import WebScraperAPI.dto.response.FavoriteResponseDto;
import WebScraperAPI.mapper.FavoriteMapper;
import WebScraperAPI.model.Client;
import WebScraperAPI.model.Favorite;
import WebScraperAPI.model.Product;
import WebScraperAPI.repository.FavoriteRepository;
import WebScraperAPI.repository.ProductRepository;
import WebScraperAPI.service.ClientService;
import WebScraperAPI.service.FavoriteService;
import com.mongodb.DuplicateKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FavoriteServiceImpl implements FavoriteService {
    private final ClientService clientService;          // 👈 buscar Client por DNI acá, no en este service
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;

    public FavoriteServiceImpl(ClientService clientService, ProductRepository productRepository, FavoriteRepository favoriteRepository, FavoriteMapper favoriteMapper) {
        this.clientService = clientService;
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
    }

    @Transactional
    public FavoriteResponseDto setFavorite(String dni, String productId, boolean desired) {
        Client client = clientService.getByDniEntity(dni);

        if (desired) {
            try {
                favoriteRepository.save(new Favorite(null, client.getId(), productId));
            } catch (DuplicateKeyException e) {
                log.debug("Favorite ya existía (clientId={}, productId={})", client.getId(), productId);
            }
            return FavoriteResponseDto.builder()
                    .productId(productId)
                    .clientDni(dni)
                    .favorite(true)
                    .build();
        } else {
            favoriteRepository.deleteByClientIdAndProductId(client.getId(), productId);
            return FavoriteResponseDto.builder()
                    .productId(productId)
                    .clientDni(dni)
                    .favorite(false)
                    .build();
        }
    }


    @Override
    public List<FavoriteResponseDto> listAll(String dni) {
        // 1) Client por DNI
        Client client = clientService.getByDniEntity(dni);

        // 2) Todos los favoritos del cliente
        List<Favorite> favs = favoriteRepository.findByClientId(client.getId());
        if (favs.isEmpty()) return List.of();

        // 3) Traer productos en bloque para evitar N+1
        List<String> productIds = favs.stream().map(Favorite::getProductId).toList();
        Map<String, Product> productsById = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        String clientDni = client.getDni();
        return favs.stream()
                .map(fav -> {
                    Product p = productsById.get(fav.getProductId());
                    String name = (p != null) ? p.getName() : "(eliminado)";
                    return favoriteMapper.toResponseDto(fav, name, clientDni);
                })
                .toList();
    }
}
