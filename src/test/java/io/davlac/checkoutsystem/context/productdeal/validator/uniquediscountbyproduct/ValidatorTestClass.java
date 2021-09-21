package io.davlac.checkoutsystem.context.productdeal.validator.uniquediscountbyproduct;

import io.davlac.checkoutsystem.productdeal.controller.validator.UniqueDiscountByProduct;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ValidatorTestClass {
    @UniqueDiscountByProduct
    private CreateProductDealRequest request;
}
