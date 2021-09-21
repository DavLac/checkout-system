package io.davlac.checkoutsystem.context.productdeal.validator;

import io.davlac.checkoutsystem.productdeal.controller.validator.ProductExists;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ValidatorTestClass {
    @ProductExists
    private Long id;
}
