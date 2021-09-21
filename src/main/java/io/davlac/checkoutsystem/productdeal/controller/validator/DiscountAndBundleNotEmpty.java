package io.davlac.checkoutsystem.productdeal.controller.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = DiscountAndBundleNotEmptyValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface DiscountAndBundleNotEmpty {
    @NotNull
    String message() default "Discount and bundles are null or empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
