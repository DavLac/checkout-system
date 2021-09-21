package io.davlac.checkoutsystem.productdeal.controller.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * To use on CreateProductDealRequest class
 */
@Constraint(validatedBy = UniqueDiscountByProductValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface UniqueDiscountByProduct {
    @NotNull
    String message() default "Product deal can have only one discount by product";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
