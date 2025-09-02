package WebScraperAPI.service.impl;

import WebScraperAPI.constant.ErrorMessages;
import WebScraperAPI.dto.ProductResponseDto;
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
    public Page<ProductResponseDto> getAllProductsAndName(int page, int size, String sortBy, String market, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Product> result;

        if (!search.isEmpty()) {
            result = productRepository.findByPageIgnoreCaseAndNameContainingIgnoreCase(
                    market.trim(), search.trim(), pageable);
        } else {
            result = productRepository.findAll(pageable);
        }
        return result.map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND + id));
        return mapper.toDto(product);
    }


}
