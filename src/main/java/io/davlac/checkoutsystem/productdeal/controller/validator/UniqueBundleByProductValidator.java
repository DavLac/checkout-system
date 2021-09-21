package io.davlac.checkoutsystem.productdeal.controller.validator;

import io.davlac.checkoutsystem.productdeal.service.ProductDealService;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class UniqueBundleByProductValidator
        implements ConstraintValidator<UniqueBundleByProduct, CreateProductDealRequest> {

    private final ProductDealService productDealService;

    public UniqueBundleByProductValidator(ProductDealService productDealService) {
        this.productDealService = productDealService;
    }

    @Override
    public boolean isValid(final CreateProductDealRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getProductId() == null || CollectionUtils.isEmpty(request.getBundles())) {
            return true;
        }

        List<ProductDealResponse> productDeals = productDealService.getAllByProductId(request.getProductId());

        return !isProductDealsHasBundle(productDeals);
    }

    private static boolean isProductDealsHasBundle(List<ProductDealResponse> productDeals) {
        return productDeals.stream()
                .anyMatch(deal -> !deal.getBundles().isEmpty());
    }
}
