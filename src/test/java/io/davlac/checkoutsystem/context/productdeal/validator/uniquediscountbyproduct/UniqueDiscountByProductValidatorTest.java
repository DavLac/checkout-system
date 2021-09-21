package io.davlac.checkoutsystem.context.productdeal.validator.uniquediscountbyproduct;

import io.davlac.checkoutsystem.productdeal.service.ProductDealService;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.DiscountRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.DiscountResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UniqueDiscountByProductValidatorTest {

    private static final long PRODUCT_ID = 123L;

    @Autowired
    private Validator validator;

    @MockBean
    private ProductDealService productDealService;

    @Test
    void isValid_withAlreadyExistingDiscount_shouldNotValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .withDiscount(DiscountRequest.builder().build())
                .build();

        ProductDealResponse productDealResponse = new ProductDealResponse();
        productDealResponse.setProductId(PRODUCT_ID);
        productDealResponse.setDiscount(new DiscountResponse());

        when(productDealService.getAllByProductId(PRODUCT_ID)).thenReturn(List.of(productDealResponse));

        Set<ConstraintViolation<ValidatorTestClass>> violations = validator.validate(new ValidatorTestClass(request));
        assertEquals(1, violations.size());
        assertEquals("Product deal can have only one discount by product", violations.stream()
                .findFirst().get().getMessage());
    }

    @Test
    void isValid_withNoProductId_shouldValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withDiscount(DiscountRequest.builder().build())
                .build();

        Set<ConstraintViolation<ValidatorTestClass>> violations = validator.validate(new ValidatorTestClass(request));
        assertEquals(0, violations.size());
    }

    @Test
    void isValid_withNoDiscount_shouldValidConstraint() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
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
