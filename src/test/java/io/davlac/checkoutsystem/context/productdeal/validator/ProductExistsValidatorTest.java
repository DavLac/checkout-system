package io.davlac.checkoutsystem.context.productdeal.validator;

import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProductExistsValidatorTest {

    private static final long PRODUCT_ID = 123L;

    @Autowired
    private Validator validator;

    @MockBean
    private ProductRepository productRepository;

    @Test
    void isValid_withNotExistingProductId_shouldNotValidConstraint() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(new Product()));

        Set<ConstraintViolation<ValidatorTestClass>> violations = validator
                .validate(new ValidatorTestClass(1L));
        assertEquals(1, violations.size());
        assertEquals("Product not found", violations.stream()
                .findFirst().get().getMessage());
    }

    @Test
    void isValid_withNullProductId_shouldNotValidConstraint() {
        Set<ConstraintViolation<ValidatorTestClass>> violations = validator
                .validate(new ValidatorTestClass(null));
        assertEquals(1, violations.size());
        assertEquals("Product not found", violations.stream()
                .findFirst().get().getMessage());
    }

    @Test
    void isValid_withExistingProductId_shouldValidConstraint() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(new Product()));

        Set<ConstraintViolation<ValidatorTestClass>> violations = validator
                .validate(new ValidatorTestClass(PRODUCT_ID));
        assertEquals(0, violations.size());
    }

}
