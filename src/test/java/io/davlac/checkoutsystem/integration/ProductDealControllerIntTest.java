package io.davlac.checkoutsystem.integration;

import io.davlac.checkoutsystem.integration.request.CreateProductDealRequestTest;
import io.davlac.checkoutsystem.integration.request.DiscountRequestTest;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.repository.ProductRepository;
import io.davlac.checkoutsystem.productdeal.controller.ProductDealController;
import io.davlac.checkoutsystem.productdeal.repository.ProductDealRepository;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.DiscountRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import io.davlac.checkoutsystem.utils.JsonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Optional;
import java.util.stream.Stream;

import static io.davlac.checkoutsystem.utils.DateUtils.assertInstantsEqualByMilli;
import static io.davlac.checkoutsystem.utils.JsonUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ProductDealController.class)
@AutoConfigureMockMvc
@EnableWebMvc
@ComponentScan("io.davlac.checkoutsystem")
class ProductDealControllerIntTest {

    private static final String PRODUCT_DEALS_URI = "/product-deals";
    private static final long PRODUCT_ID = 123L;
    private static final String NAME = "product_name";
    private static final String DESCRIPTION = "description";
    private static final double PRICE = 12.34;
    private static final int DISCOUNT_50 = 50;
    private static final int DISCOUNT_70 = 70;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private ProductDealRepository productDealRepository;

    @Autowired
    private ProductRepository productRepository;

    Product savedProduct = new Product();

    @BeforeEach
    public void setUp() {
        Product product = new Product();
        product.setName(NAME);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);

        savedProduct = productRepository.save(product);
    }

    @AfterEach
    public void clean() {
        productDealRepository.deleteAll();
        productRepository.deleteAll();
    }

    public static Stream<Arguments> createDealParameters() {
        return Stream.of(
                Arguments.of("50% discount on product id",
                        CreateProductDealRequestTest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequestTest.builder()
                                                .withDiscountPercentage(DISCOUNT_50)
                                                .withTotalDiscountedItems(1)
                                                .withTotalFullPriceItems(0)
                                                .build()
                                )
                                .build()),
                Arguments.of("Buy 2, third 70%",
                        CreateProductDealRequestTest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequestTest.builder()
                                                .withDiscountPercentage(DISCOUNT_70)
                                                .withTotalDiscountedItems(1)
                                                .withTotalFullPriceItems(2)
                                                .build()
                                )
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createDealParameters")
    void create_withGoodData_shouldReturnSavedDeal(String test,
                                                   CreateProductDealRequestTest req) throws Exception {
        int dealsBefore = productDealRepository.findAll().size();

        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(savedProduct.getId())
                .withDiscount(
                        DiscountRequest.builder()
                                .withDiscountPercentage(req.getDiscount().getDiscountPercentage())
                                .withTotalDiscountedItems(req.getDiscount().getTotalDiscountedItems())
                                .withTotalFullPriceItems(req.getDiscount().getTotalFullPriceItems())
                                .build()
                )
                .build();

        // make request
        ResultActions resultActions = mockMvc.perform(post(PRODUCT_DEALS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());

        ProductDealResponse response = (ProductDealResponse) jsonUtils
                .deserializeResult(resultActions, ProductDealResponse.class);

        assertTrue(response.getId() > 0);
        assertNotNull(response.getLastModifiedDate());

        // assertions product
        assertNotNull(response.getProduct());
        assertEquals(request.getProductId(), response.getProduct().getId());
        assertEquals(savedProduct.getName(), response.getProduct().getName());
        assertEquals(savedProduct.getDescription(), response.getProduct().getDescription());
        assertEquals(savedProduct.getPrice(), response.getProduct().getPrice());
        assertInstantsEqualByMilli(savedProduct.getLastModifiedDate(), response.getProduct().getLastModifiedDate());

        // assertions discount
        if (request.getDiscount() != null) {
            assertTrue(response.getDiscount().getId() > 0);
            assertEquals(request.getDiscount().getDiscountPercentage(), response.getDiscount().getDiscountPercentage());
            assertEquals(request.getDiscount().getTotalDiscountedItems(), response.getDiscount().getTotalDiscountedItems());
            assertEquals(request.getDiscount().getTotalFullPriceItems(), response.getDiscount().getTotalFullPriceItems());
            assertNotNull(response.getDiscount().getLastModifiedDate());
        }

        // database assertions
        int dealsAfter = productDealRepository.findAll().size();
        assertEquals(dealsBefore + 1, dealsAfter);
        assertEquals(1, dealsAfter);
        assertNotEquals(Optional.empty(), productDealRepository.findById(response.getId()));
    }

    public static Stream<Arguments> createDealErrorParameters() {
        return Stream.of(
                Arguments.of("No product id",
                        CreateProductDealRequest.builder()
                                .build()),
                Arguments.of("Discounted items 0",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(DISCOUNT_50)
                                                .withTotalDiscountedItems(0)
                                                .withTotalFullPriceItems(0)
                                                .build()
                                )
                                .build()),
                Arguments.of("Discount percentage negative",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(-10)
                                                .withTotalDiscountedItems(1)
                                                .withTotalFullPriceItems(0)
                                                .build()
                                )
                                .build()),
                Arguments.of("Discount percentage more than 100",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(101)
                                                .withTotalDiscountedItems(1)
                                                .withTotalFullPriceItems(0)
                                                .build()
                                )
                                .build()),
                Arguments.of("Discounted negative",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(0)
                                                .withTotalDiscountedItems(-1)
                                                .withTotalFullPriceItems(0)
                                                .build()
                                )
                                .build()),
                Arguments.of("Full price negative",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(0)
                                                .withTotalDiscountedItems(1)
                                                .withTotalFullPriceItems(-1)
                                                .build()
                                )
                                .build()),
                Arguments.of("Discount empty",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .build()
                                )
                                .build()),
                Arguments.of("No discount percentage",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withTotalDiscountedItems(1)
                                                .withTotalFullPriceItems(0)
                                                .build()
                                )
                                .build()),
                Arguments.of("No discounted",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(0)
                                                .withTotalFullPriceItems(0)
                                                .build()
                                )
                                .build()),
                Arguments.of("No full price",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(0)
                                                .withDiscountPercentage(1)
                                                .build()
                                )
                                .build())
        );
    }

    @ParameterizedTest
    @MethodSource("createDealErrorParameters")
    void create_withBadRequest_shouldThrowBadRequest(String test,
                                                     CreateProductDealRequest req) throws Exception {
        mockMvc.perform(post(PRODUCT_DEALS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(req)))
                .andExpect(status().isBadRequest());
    }
}
