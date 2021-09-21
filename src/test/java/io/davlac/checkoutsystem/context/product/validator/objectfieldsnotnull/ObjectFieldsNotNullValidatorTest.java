package io.davlac.checkoutsystem.context.product.validator.objectfieldsnotnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjectFieldsNotNullValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_withNullObject_shouldNotValidConstraint() {
        Set<ConstraintViolation<ValidatorTestClass>> violations = validator
                .validate(new ValidatorTestClass(null));
        assertEquals(1, violations.size());
        assertEquals("All the fields are null", violations.stream().findFirst().get().getMessage());
    }

    @Test
    void isValid_withNullFieldsObject_shouldNotValidConstraint() {
        Set<ConstraintViolation<ValidatorTestClass>> violations = validator
                .validate(new ValidatorTestClass(new InnerValidatorTestClass()));
        assertEquals(1, violations.size());
        assertEquals("All the fields are null", violations.stream().findFirst().get().getMessage());
    }

    @Test
    void isValid_withNotNullFieldsObject_shouldValidConstraint() {
        ValidatorTestClass validatorTestClass = new ValidatorTestClass(new InnerValidatorTestClass("", 0, List.of()));
        Set<ConstraintViolation<ValidatorTestClass>> violations = validator.validate(validatorTestClass);
        assertEquals(0, violations.size());
    }

    @Test
    void isValid_withOneNullFieldsObject_shouldValidConstraint() {
        ValidatorTestClass validatorTestClass = new ValidatorTestClass(new InnerValidatorTestClass(null, 0, List.of()));
        Set<ConstraintViolation<ValidatorTestClass>> violations = validator.validate(validatorTestClass);
        assertEquals(0, violations.size());
    }
}
