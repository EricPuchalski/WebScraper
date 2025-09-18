package WebScraperAPI.service.impl;

import WebScraperAPI.constant.ErrorMessages;
import WebScraperAPI.dto.response.ProductResponseDto;
import WebScraperAPI.exception.EntityNotFoundException;
import WebScraperAPI.mapper.ProductMapper;
import WebScraperAPI.model.Product;
import WebScraperAPI.repository.ProductRepository;
import WebScraperAPI.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    public Page<ProductResponseDto> getAllProductsAndName(int pageNumber, int size, String sortBy, String market, String search) {
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(sortBy).ascending());

        boolean hasMarket = StringUtils.hasText(market);
        boolean hasSearch = StringUtils.hasText(search);

        Page<Product> result = findProducts(hasMarket, hasSearch, market, search, pageable);
        return result.map(mapper::toDto);
    }

    private Page<Product> findProducts(boolean hasMarket, boolean hasSearch, String market, String search, Pageable pageable) {
        if (hasMarket && hasSearch) {
            return productRepository.findByPageIgnoreCaseAndNameContainingIgnoreCase(market.trim(), search.trim(), pageable);
        }
        if (hasMarket) {
            return productRepository.findByPageIgnoreCase(market.trim(), pageable);
        }
        if (hasSearch) {
            return productRepository.findByNameContainingIgnoreCase(search.trim(), pageable);
        }
        return productRepository.findAll(pageable);
    }
    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND + id));
        return mapper.toDto(product);
    }


}
