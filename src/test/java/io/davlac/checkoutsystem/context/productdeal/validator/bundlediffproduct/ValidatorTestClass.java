package io.davlac.checkoutsystem.context.productdeal.validator.bundlediffproduct;

import io.davlac.checkoutsystem.productdeal.controller.validator.BundleProductDifferentThanTargetProduct;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ValidatorTestClass {
    @BundleProductDifferentThanTargetProduct
    private CreateProductDealRequest createProductDealRequest;
}
