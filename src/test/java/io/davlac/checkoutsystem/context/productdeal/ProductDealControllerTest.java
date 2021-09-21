package io.davlac.checkoutsystem.context.productdeal;

import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.repository.ProductRepository;
import io.davlac.checkoutsystem.productdeal.controller.ProductDealController;
import io.davlac.checkoutsystem.productdeal.service.ProductDealService;
import io.davlac.checkoutsystem.productdeal.service.dto.request.BundleRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.DiscountRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProductDealControllerTest {

    private static final Long ID = 123L;
    private static final long PRODUCT_ID = 123L;
    private static final long PRODUCT_ID_2 = 456L;

    @MockBean
    private ProductDealService productDealService;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductDealController productDealController;

    @Autowired
    private Validator validator;

    ProductDealResponse productDealResponse = new ProductDealResponse();

    @BeforeEach
    public void setUp() {
        productDealResponse.setId(ID);
    }

    @Test
    void create_withGoodRequest_shouldReturnSavedProductDeal() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .withBundles(Set.of(BundleRequest.builder()
                        .withProductId(PRODUCT_ID_2)
                        .withDiscountPercentage(1).build()))
                .build();

        when(productDealService.create(request)).thenReturn(productDealResponse);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(new Product()));
        when(productRepository.findById(PRODUCT_ID_2)).thenReturn(Optional.of(new Product()));

        ResponseEntity<ProductDealResponse> response = productDealController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(ID, response.getBody().getId());
    }

    public static Stream<Arguments> createProductDealBadRequestParameters() {
        return Stream.of(
                Arguments.of(
                        "No product ID",
                        CreateProductDealRequest.builder()
                                .build(),
                        "must not be null"
                ),
                Arguments.of(
                        "No discount percentage",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withTotalFullPriceItems(1)
                                                .withTotalDiscountedItems(1)
                                                .build()
                                )
                                .build(),
                        "must not be null"
                ),
                Arguments.of(
                        "No TotalFullPriceItems",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(1)
                                                .withTotalDiscountedItems(1)
                                                .build()
                                )
                                .build(),
                        "must not be null"
                ),
                Arguments.of(
                        "No TotalDiscountedItems",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(1)
                                                .withTotalFullPriceItems(1)
                                                .build()
                                )
                                .build(),
                        "must not be null"
                ),
                Arguments.of(
                        "TotalDiscountedItems 0",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(1)
                                                .withTotalFullPriceItems(1)
                                                .withTotalDiscountedItems(0)
                                                .build()
                                )
                                .build(),
                        "must be greater than or equal to 1"
                ),
                Arguments.of(
                        "TotalFullPriceItems negative",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(1)
                                                .withTotalFullPriceItems(-1)
                                                .withTotalDiscountedItems(1)
                                                .build()
                                )
                                .build(),
                        "must be greater than or equal to 0"
                ),
                Arguments.of(
                        "Discount percentage negative",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(-1)
                                                .withTotalFullPriceItems(1)
                                                .withTotalDiscountedItems(1)
                                                .build()
                                )
                                .build(),
                        "must be greater than or equal to 0"
                ),
                Arguments.of(
                        "Discount percentage too big",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(101)
                                                .withTotalFullPriceItems(1)
                                                .withTotalDiscountedItems(1)
                                                .build()
                                )
                                .build(),
                        "must be less than or equal to 100"
                ),
                Arguments.of(
                        "Bundle no product ID",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withBundles(Set.of(
                                        BundleRequest.builder()
                                                .withDiscountPercentage(1)
                                                .build()
                                        )
                                )
                                .build(),
                        "must not be null"
                ),
                Arguments.of(
                        "Bundle no percentage",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withBundles(Set.of(
                                        BundleRequest.builder()
                                                .withProductId(PRODUCT_ID)
                                                .build()
                                        )
                                )
                                .build(),
                        "must not be null"
                ),
                Arguments.of(
                        "Bundle percentage negative",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withBundles(Set.of(
                                        BundleRequest.builder()
                                                .withProductId(PRODUCT_ID)
                                                .withDiscountPercentage(-1)
                                                .build()
                                        )
                                )
                                .build(),
                        "must be greater than or equal to 0"
                ),
                Arguments.of(
                        "Bundle percentage too high",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withBundles(Set.of(
                                        BundleRequest.builder()
                                                .withProductId(PRODUCT_ID)
                                                .withDiscountPercentage(101)
                                                .build()
                                        )
                                )
                                .build(),
                        "must be less than or equal to 100"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createProductDealBadRequestParameters")
    void create_withError_shouldFailValidation(String test,
                                               CreateProductDealRequest request,
                                               String errorMessage) {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(new Product()));
        Set<ConstraintViolation<CreateProductDealRequest>> violations = validator.validate(request);
        assertNotEquals(0, violations.size());
        assertTrue(violations.stream().anyMatch(msg -> msg.getMessage().equals(errorMessage)));
    }

    @Test
    void deleteById_withExistingProductDeal_shouldDeleteProductDeal() {
        ResponseEntity<Void> response = productDealController.deleteById(ID);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
