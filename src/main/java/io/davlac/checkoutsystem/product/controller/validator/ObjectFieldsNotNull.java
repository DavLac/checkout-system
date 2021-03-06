package io.davlac.checkoutsystem.product.controller.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ObjectFieldsNotNullValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ObjectFieldsNotNull {
    @NotNull
    String message() default "All the fields are null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
