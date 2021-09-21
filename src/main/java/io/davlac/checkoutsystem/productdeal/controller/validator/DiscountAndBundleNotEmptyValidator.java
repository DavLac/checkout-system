package io.davlac.checkoutsystem.productdeal.controller.validator;

import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DiscountAndBundleNotEmptyValidator
        implements ConstraintValidator<DiscountAndBundleNotEmpty, CreateProductDealRequest> {

    @Override
    public boolean isValid(final CreateProductDealRequest request, ConstraintValidatorContext context) {
        return !CollectionUtils.isEmpty(request.getBundles()) || request.getDiscount() != null;
    }
}
