package io.davlac.checkoutsystem.product.service;

import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.repository.ProductRepository;
import io.davlac.checkoutsystem.product.service.dto.CreateProductRequest;
import io.davlac.checkoutsystem.product.service.dto.CreateProductResponse;
import io.davlac.checkoutsystem.product.service.mapper.ProductMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public CreateProductResponse create(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product productSaved = productRepository.save(product);
        return productMapper.toDto(productSaved);
    }
}
