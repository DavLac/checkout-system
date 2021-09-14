package io.davlac.checkoutsystem.productdeal.service;

import io.davlac.checkoutsystem.product.controller.error.BadRequestException;
import io.davlac.checkoutsystem.product.controller.error.NotFoundException;
import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import io.davlac.checkoutsystem.productdeal.repository.ProductDealRepository;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import io.davlac.checkoutsystem.productdeal.service.mapper.ProductDealMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
        checkProductDealRequest(request);
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

    private static void checkProductDealRequest(CreateProductDealRequest request) {
        if (CollectionUtils.isEmpty(request.getBundles()) && request.getDiscount() == null) {
            throw new BadRequestException("Discount and bundles are empty or null");
        }

        if (!CollectionUtils.isEmpty(request.getBundles()) &&
                request.getDiscount() != null &&
                request.getDiscount().getTotalDiscountedItems() > 1 &&
                request.getDiscount().getTotalFullPriceItems() > 0) {
            throw new BadRequestException("Product deal cannot have a grouped discount and a bundle in the same time");
        }
    }


}
