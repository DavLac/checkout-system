package io.davlac.checkoutsystem.context.product.validator.discountbundlenotempty;

import io.davlac.checkoutsystem.productdeal.service.dto.request.BundleRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.DiscountRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ObjectFieldsNotNullValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void isValid_withNullDiscountAndBundle_shouldNotValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder().build();
        Set<ConstraintViolation<ValidatorTestClass>> violations = validator
                .validate(new ValidatorTestClass(request));
        assertEquals(1, violations.size());
        assertEquals("Discount and bundles are null or empty", violations.stream()
                .findFirst().get().getMessage());
    }

    @Test
    void isValid_withBundleEmpty_shouldNotValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withBundles(Set.of())
                .build();
        Set<ConstraintViolation<ValidatorTestClass>> violations = validator
                .validate(new ValidatorTestClass(request));
        assertEquals(1, violations.size());
        assertEquals("Discount and bundles are null or empty", violations.stream()
                .findFirst().get().getMessage());
    }

    @Test
    void isValid_withDiscountNotNull_shouldValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withDiscount(DiscountRequest.builder().build())
                .build();
        Set<ConstraintViolation<ValidatorTestClass>> violations = validator
                .validate(new ValidatorTestClass(request));
        assertEquals(0, violations.size());
    }

    @Test
    void isValid_withBundleNotEmpty_shouldValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withBundles(Set.of(
                        BundleRequest.builder().build()
                ))
                .build();
        Set<ConstraintViolation<ValidatorTestClass>> violations = validator
                .validate(new ValidatorTestClass(request));
        assertEquals(0, violations.size());
    }

}
