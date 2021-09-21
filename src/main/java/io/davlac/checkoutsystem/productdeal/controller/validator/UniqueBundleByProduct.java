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
@Constraint(validatedBy = UniqueBundleByProductValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface UniqueBundleByProduct {
    @NotNull
    String message() default "Product deal can have only one bundle by product";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
