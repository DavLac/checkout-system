package io.davlac.checkoutsystem.productdeal.service;

import io.davlac.checkoutsystem.product.controller.error.BadRequestException;
import io.davlac.checkoutsystem.product.controller.error.NotFoundException;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.service.ProductService;
import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import io.davlac.checkoutsystem.productdeal.repository.ProductDealRepository;
import io.davlac.checkoutsystem.productdeal.service.dto.request.BundleRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import io.davlac.checkoutsystem.productdeal.service.mapper.ProductDealMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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

    @Transactional(readOnly = true)
    public List<ProductDealResponse> getAllByProductId(final long productId) {
        Product product = productService.getEntityById(productId);
        List<ProductDeal> productDeals = productDealRepository.findAllByProduct(product);
        return productDeals.stream()
                .map(productDealMapper::toDto)
                .collect(Collectors.toList());
    }

    private void checkProductDealRequest(CreateProductDealRequest request) {
        if (CollectionUtils.isEmpty(request.getBundles()) && request.getDiscount() == null) {
            throw new BadRequestException("Discount and bundles are empty or null");
        }

        // check if product exist
        productService.getEntityById(request.getProductId());

        // check bundles
        if (!CollectionUtils.isEmpty(request.getBundles())) {
            if (isBundleContainsProductDeal(request)) {
                throw new BadRequestException("A bundle cannot contain the deal product");
            }

            // check if all bundle's product exist
            request.getBundles().stream()
                    .map(BundleRequest::getProductId)
                    .forEach(productService::getEntityById);
        }

        List<ProductDealResponse> productDeals = getAllByProductId(request.getProductId());
        if (!CollectionUtils.isEmpty(request.getBundles()) && isProductDealsHasBundle(productDeals)) {
            throw new BadRequestException("Product deal can have only one bundle by product");
        }

        // check discounts
        if (request.getDiscount() != null && isProductDealsHasDiscount(productDeals)) {
            throw new BadRequestException("Product deal can have only one discount by product");
        }
    }

    private static boolean isProductDealsHasDiscount(List<ProductDealResponse> productDeals) {
        return productDeals.stream()
                .anyMatch(deal -> deal.getDiscount() != null);
    }

    private static boolean isProductDealsHasBundle(List<ProductDealResponse> productDeals) {
        return productDeals.stream()
                .anyMatch(deal -> !deal.getBundles().isEmpty());
    }

    private static boolean isBundleContainsProductDeal(CreateProductDealRequest request) {
        return request.getBundles().stream()
                .anyMatch(bundle -> bundle.getProductId().equals(request.getProductId()));
    }

}
