package io.davlac.checkoutsystem.productdeal.controller.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ProductExistsValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ProductExists {
    @NotNull
    String message() default "Product not found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
