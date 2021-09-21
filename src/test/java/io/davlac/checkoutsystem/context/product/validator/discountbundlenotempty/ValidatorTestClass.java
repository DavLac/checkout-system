package io.davlac.checkoutsystem.context.product.validator.discountbundlenotempty;

import io.davlac.checkoutsystem.productdeal.controller.validator.DiscountAndBundleNotEmpty;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ValidatorTestClass {
    @DiscountAndBundleNotEmpty
    private CreateProductDealRequest createProductDealRequest;
}
