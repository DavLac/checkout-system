package io.davlac.checkoutsystem.product.service;

import io.davlac.checkoutsystem.product.controller.error.NotFoundException;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.repository.ProductRepository;
import io.davlac.checkoutsystem.product.service.dto.CreateProductRequest;
import io.davlac.checkoutsystem.product.service.dto.ProductResponse;
import io.davlac.checkoutsystem.product.service.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product productSaved = productRepository.save(product);
        return productMapper.toDto(productSaved);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(long id) {
        Product product = getEntityById(id);
        return productMapper.toDto(product);
    }

    @Transactional
    public void deleteById(long id) {
        Product product = getEntityById(id);
        productRepository.delete(product);
    }

    private Product getEntityById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }
}
