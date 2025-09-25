package WebScraperAPI.service.impl;

import WebScraperAPI.constant.ErrorMessages;
import WebScraperAPI.dto.response.ProductResponseDto;
import WebScraperAPI.exception.EntityNotFoundException;
import WebScraperAPI.mapper.ProductMapper;
import WebScraperAPI.model.Product;
import WebScraperAPI.repository.ProductRepository;
import WebScraperAPI.service.ProductService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProductsAndName(
            int pageNumber, int size, String sortBy, String sortDir, List<String> markets, String search
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        boolean hasMarkets = markets != null && !markets.isEmpty();
        boolean hasSearch  = StringUtils.hasText(search);

        Page<Product> result = findProducts(hasMarkets, hasSearch, markets, search, pageable);
        return result.map(mapper::toDto);
    }

    private Page<Product> findProducts(
            boolean hasMarkets, boolean hasSearch, List<String> markets, String search, Pageable pageable
    ) {
        if (hasMarkets && hasSearch) return productRepository.findByPageInAndNameContainingIgnoreCase(markets, search.trim(), pageable);
        if (hasMarkets) return productRepository.findByPageIn(markets, pageable);
        if (hasSearch) return productRepository.findByNameContainingIgnoreCase(search.trim(), pageable);
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND + id));
        return mapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getOffers(
            int pageNumber, int size, String sortBy, String sortDir, List<String> markets, String search
    ) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        boolean hasMarkets = markets != null && !markets.isEmpty();
        boolean hasSearch  = StringUtils.hasText(search);

        Page<Product> result = findOffers(hasMarkets, hasSearch, markets, search, pageable);
        return result.map(mapper::toDto);
    }

    private Page<Product> findOffers(
            boolean hasMarkets, boolean hasSearch, List<String> markets, String search, Pageable pageable
    ) {
        if (hasMarkets && hasSearch) return productRepository.findByHasPriceDroppedTrueAndPageInAndNameContainingIgnoreCase(markets, search.trim(), pageable);

        if (hasMarkets) return productRepository.findByHasPriceDroppedTrueAndPageIn(markets, pageable);

        if (hasSearch) return productRepository.findByHasPriceDroppedTrueAndNameContainingIgnoreCase(search.trim(), pageable);

        return productRepository.findByHasPriceDroppedTrue(pageable);
    }

}
