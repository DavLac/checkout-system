package io.davlac.checkoutsystem.productdeal.service;

import io.davlac.checkoutsystem.product.controller.error.NotFoundException;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.service.ProductService;
import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import io.davlac.checkoutsystem.productdeal.repository.ProductDealRepository;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import io.davlac.checkoutsystem.productdeal.service.mapper.ProductDealMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductDealService {

    private final ProductDealRepository productDealRepository;
    private final ProductDealMapper productDealMapper;
    private final ProductService productService;

    @Transactional
    public ProductDealResponse create(@NotNull final CreateProductDealRequest request) {
        ProductDeal productDeal = productDealMapper.toEntity(request);
        ProductDeal productDealSaved = productDealRepository.save(productDeal);
        return productDealMapper.toDto(productDealSaved);
    }

    @Transactional
    public void deleteById(final long id) {
        ProductDeal productDeal = productDealRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product deal not found"));
        productDealRepository.delete(productDeal);
    }

    @Transactional(readOnly = true)
    public List<ProductDealResponse> getAllByProductId(final long productId) {
        Product product = productService.getEntityById(productId);
        List<ProductDeal> productDeals = productDealRepository.findAllByProduct(product);
        return productDeals.stream()
                .map(productDealMapper::toDto)
                .collect(Collectors.toList());
    }

}
