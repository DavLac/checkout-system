package io.davlac.checkoutsystem.context.product;

import io.davlac.checkoutsystem.product.controller.ProductController;
import io.davlac.checkoutsystem.product.service.ProductService;
import io.davlac.checkoutsystem.product.service.dto.CreateProductRequest;
import io.davlac.checkoutsystem.product.service.dto.ProductResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private static final Long ID = 123L;
    private static final String NAME = "product_name";
    private static final String DESCRIPTION = "description";
    private static final double PRICE = 12.34;
    private static final String DESCRIPTION_2 = "description-2";
    private static final double PRICE_2 = 45.67;
    private static final Instant LAST_MODIFIED_DATE = Instant.now();

    private static final double PRICE_TOO_MANY_DECIMALS = 12.456;
    private static final double PRICE_TOO_MANY_INT = 12345678912.0;
    private static final double PRICE_ZERO = 0;
    private static final double PRICE_NEGATIVE = -10;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    ProductResponse productResponse = new ProductResponse();
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        productResponse.setId(ID);
        productResponse.setPrice(PRICE);
        productResponse.setDescription(DESCRIPTION);
        productResponse.setName(NAME);
        productResponse.setLastModifiedDate(LAST_MODIFIED_DATE);
    }

    @Test
    void create_withGoodRequest_shouldSavedProduct() {
        CreateProductRequest request = CreateProductRequest.builder()
                .withName(NAME)
                .withDescription(DESCRIPTION)
                .withPrice(PRICE)
                .build();

        when(productService.create(request)).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertEquals(ID, response.getBody().getId());
        assertEquals(request.getName(), response.getBody().getName());
        assertEquals(request.getPrice(), response.getBody().getPrice());
        assertEquals(request.getDescription(), response.getBody().getDescription());
        assertEquals(LAST_MODIFIED_DATE, response.getBody().getLastModifiedDate());
    }

    public static Stream<Arguments> createProductBadRequestParameters() {
        return Stream.of(
                Arguments.of(
                        "No name",
                        CreateProductRequest.builder()
                                .withPrice(PRICE)
                                .build(),
                        "must not be empty"
                ),
                Arguments.of(
                        "Too short name",
                        CreateProductRequest.builder()
                                .withName(RandomStringUtils.randomAlphabetic(2))
                                .withPrice(PRICE)
                                .build(),
                        "size must be between 3 and 30"
                ),
                Arguments.of(
                        "Too long name",
                        CreateProductRequest.builder()
                                .withName(RandomStringUtils.randomAlphabetic(31))
                                .withPrice(PRICE)
                                .build(),
                        "size must be between 3 and 30"
                ),
                Arguments.of(
                        "Too short description",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE)
                                .withDescription(RandomStringUtils.randomAlphabetic(2))
                                .build(),
                        "size must be between 3 and 100"
                ),
                Arguments.of(
                        "Too long description",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE)
                                .withDescription(RandomStringUtils.randomAlphabetic(101))
                                .build(),
                        "size must be between 3 and 100"
                ),
                Arguments.of(
                        "No price",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .build(),
                        "must not be null"
                ),

                Arguments.of(
                        "Price too many decimals",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE_TOO_MANY_DECIMALS)
                                .build(),
                        "numeric value out of bounds (<10 digits>.<2 digits> expected)"
                ),
                Arguments.of(
                        "Price negative",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE_NEGATIVE)
                                .build(),
                        "must be greater than 0"
                ),
                Arguments.of(
                        "Price zero",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE_ZERO)
                                .build(),
                        "must be greater than 0"
                ),
                Arguments.of(
                        "Price too big",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE_TOO_MANY_INT)
                                .build(),
                        "numeric value out of bounds (<10 digits>.<2 digits> expected)"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createProductBadRequestParameters")
    void create_withNoName_shouldFailValidation(String test,
                                                CreateProductRequest request,
                                                String errorMessage) {
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(errorMessage, violations.stream().findFirst().get().getMessage());
    }
}
