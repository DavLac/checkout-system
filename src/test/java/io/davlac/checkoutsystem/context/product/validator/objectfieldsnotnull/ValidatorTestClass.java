package io.davlac.checkoutsystem.context.product.validator.objectfieldsnotnull;

import io.davlac.checkoutsystem.product.controller.validator.ObjectFieldsNotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ValidatorTestClass {
    @ObjectFieldsNotNull
    private InnerValidatorTestClass innerValidatorTestClass;
}
