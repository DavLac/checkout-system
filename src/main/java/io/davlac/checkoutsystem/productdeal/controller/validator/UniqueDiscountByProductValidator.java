package io.davlac.checkoutsystem.productdeal.controller.validator;

import io.davlac.checkoutsystem.productdeal.service.ProductDealService;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class UniqueDiscountByProductValidator
        implements ConstraintValidator<UniqueDiscountByProduct, CreateProductDealRequest> {

    private final ProductDealService productDealService;

    public UniqueDiscountByProductValidator(ProductDealService productDealService) {
        this.productDealService = productDealService;
    }

    @Override
    public boolean isValid(final CreateProductDealRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getProductId() == null || request.getDiscount() == null) {
            return true;
        }

        List<ProductDealResponse> productDeals = productDealService.getAllByProductId(request.getProductId());

        return !isProductDealsHasDiscount(productDeals);
    }

    private static boolean isProductDealsHasDiscount(List<ProductDealResponse> productDeals) {
        return productDeals.stream()
                .anyMatch(deal -> deal.getDiscount() != null);
    }
}
