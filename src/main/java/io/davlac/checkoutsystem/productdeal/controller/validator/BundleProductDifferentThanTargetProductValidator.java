package io.davlac.checkoutsystem.productdeal.controller.validator;

import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BundleProductDifferentThanTargetProductValidator
        implements ConstraintValidator<BundleProductDifferentThanTargetProduct, CreateProductDealRequest> {

    @Override
    public boolean isValid(final CreateProductDealRequest request, ConstraintValidatorContext context) {
        if (request == null || CollectionUtils.isEmpty(request.getBundles())) {
            return true;
        }

        return !isBundleContainsProductDeal(request);
    }

    private static boolean isBundleContainsProductDeal(CreateProductDealRequest request) {
        return request.getBundles().stream()
                .anyMatch(bundle -> bundle.getProductId().equals(request.getProductId()));
    }
}
