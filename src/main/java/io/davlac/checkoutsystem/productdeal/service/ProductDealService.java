package io.davlac.checkoutsystem.productdeal.service;

import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import io.davlac.checkoutsystem.productdeal.repository.ProductDealRepository;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import io.davlac.checkoutsystem.productdeal.service.mapper.ProductDealMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Service
public class ProductDealService {

    private final ProductDealRepository productDealRepository;
    private final ProductDealMapper productDealMapper;

    public ProductDealService(ProductDealRepository productDealRepository,
                              ProductDealMapper productDealMapper) {
        this.productDealRepository = productDealRepository;
        this.productDealMapper = productDealMapper;
    }

    @Transactional
    public ProductDealResponse create(@NotNull final CreateProductDealRequest request) {
        ProductDeal product = productDealMapper.toEntity(request);
        ProductDeal productSaved = productDealRepository.save(product);
        return productDealMapper.toDto(productSaved);
    }
}
