package io.davlac.checkoutsystem.context.productdeal.validator.bundlediffproduct;

import io.davlac.checkoutsystem.productdeal.service.dto.request.BundleRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BundleProductDifferentThanTargetProductValidatorTest {

    private static final long PRODUCT_ID = 123L;
    public static final long PRODUCT_ID_2 = 456L;

    @Autowired
    private Validator validator;

    @Test
    void isValid_withBundleProductSameThanTargetProduct_shouldNotValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .withBundles(Set.of(BundleRequest.builder()
                        .withProductId(PRODUCT_ID)
                        .build()))
                .build();

        Set<ConstraintViolation<ValidatorTestClass>> violations = validator.validate(new ValidatorTestClass(request));
        assertEquals(1, violations.size());
        assertEquals("Bundle product ID is the same than targeted product ID", violations.stream()
                .findFirst().get().getMessage());
    }

    @Test
    void isValid_withBundleProductDifferentThanTargetProduct_shouldValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .withBundles(Set.of(BundleRequest.builder()
                        .withProductId(PRODUCT_ID_2)
                        .build()))
                .build();

        Set<ConstraintViolation<ValidatorTestClass>> violations = validator.validate(new ValidatorTestClass(request));
        assertEquals(0, violations.size());
    }

    @Test
    void isValid_withRequestEmpty_shouldValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .build();

        Set<ConstraintViolation<ValidatorTestClass>> violations = validator.validate(new ValidatorTestClass(request));
        assertEquals(0, violations.size());
    }

    @Test
    void isValid_withNoBundle_shouldValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .build();

        Set<ConstraintViolation<ValidatorTestClass>> violations = validator.validate(new ValidatorTestClass(request));
        assertEquals(0, violations.size());
    }

    @Test
    void isValid_withBundleEmpty_shouldValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .withBundles(Set.of())
                .build();

        Set<ConstraintViolation<ValidatorTestClass>> violations = validator.validate(new ValidatorTestClass(request));
        assertEquals(0, violations.size());
    }

    @Test
    void isValid_withNullRequest_shouldValidConstraint() {
        Set<ConstraintViolation<ValidatorTestClass>> violations = validator.validate(new ValidatorTestClass(null));
        assertEquals(0, violations.size());
    }
}
