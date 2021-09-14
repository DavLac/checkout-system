package io.davlac.checkoutsystem.product.service;

import io.davlac.checkoutsystem.product.controller.error.BadRequestException;
import io.davlac.checkoutsystem.product.controller.error.NotFoundException;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.repository.ProductRepository;
import io.davlac.checkoutsystem.product.service.dto.CreateProductRequest;
import io.davlac.checkoutsystem.product.service.dto.PatchProductRequest;
import io.davlac.checkoutsystem.product.service.dto.ProductResponse;
import io.davlac.checkoutsystem.product.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse create(@NotNull final CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product productSaved = productRepository.save(product);
        return productMapper.toDto(productSaved);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(final long id) {
        Product product = getEntityById(id);
        return productMapper.toDto(product);
    }

    @Transactional
    public void deleteById(final long id) {
        Product product = getEntityById(id);
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse patchById(final long id, @NotNull final PatchProductRequest request) {
        if (request.isEmpty()) {
            throw new BadRequestException("Body is empty");
        }

        Product product = getEntityById(id);
        productMapper.updateEntity(request, product);
        Product productPatched = productRepository.save(product);
        return productMapper.toDto(productPatched);
    }

    public Product getEntityById(final long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }
}
