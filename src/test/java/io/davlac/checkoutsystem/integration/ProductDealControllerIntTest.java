package io.davlac.checkoutsystem.integration;

import io.davlac.checkoutsystem.config.JsonUtils;
import io.davlac.checkoutsystem.productdeal.controller.ProductDealController;
import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import io.davlac.checkoutsystem.productdeal.repository.ProductDealRepository;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.DiscountRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
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

import java.util.stream.Stream;

import static io.davlac.checkoutsystem.config.JsonUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private static final int DISCOUNT_50 = 50;
    private static final int DISCOUNT_70 = 70;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private ProductDealRepository productDealRepository;

    public static Stream<Arguments> createDealParameters() {
        return Stream.of(
                Arguments.of(
                        "50% discount on product id",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
                                                .withDiscountPercentage(DISCOUNT_50)
                                                .withTotalDiscountedItems(1)
                                                .withTotalFullPriceItems(0)
                                                .build()
                                )
                                .build()
                ),
                Arguments.of(
                        "Buy 2, third 70%",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withDiscount(
                                        DiscountRequest.builder()
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
                                                   CreateProductDealRequest request) throws Exception {
        int dealsBefore = productDealRepository.findAll().size();

        // make request
        ResultActions resultActions = mockMvc.perform(post(PRODUCT_DEALS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());

        ProductDealResponse response = (ProductDealResponse) jsonUtils
                .deserializeResult(resultActions, ProductDealResponse.class);

        assertTrue(response.getId() > 0);
        assertEquals(request.getProductId(), response.getProductId());
        assertEquals(request.getDiscount().getDiscountPercentage(),
                response.getDiscount().getDiscountPercentage());
        assertEquals(request.getDiscount().getTotalDiscountedItems(),
                response.getDiscount().getTotalDiscountedItems());
        assertEquals(request.getDiscount().getTotalFullPriceItems(),
                response.getDiscount().getTotalFullPriceItems());
        assertNotNull(response.getLastModifiedDate());

        // database assertions
        int dealsAfter = productDealRepository.findAll().size();
        assertEquals(dealsBefore + 1, dealsAfter);
        assertEquals(1, dealsAfter);

        ProductDeal productDeal = productDealRepository.findById(response.getId()).get();
        assertTrue(productDeal.getId() > 0);
        assertEquals(request.getProductId(), productDeal.getProductId());
        /*assertEquals(request.getDiscount().getDiscountPercentage(),
                productDeal.getDiscount().getDiscountPercentage());
        assertEquals(request.getDiscount().getTotalDiscountedItems(),
                productDeal.getDiscount().getTotalDiscountedItems());
        assertEquals(request.getDiscount().getTotalFullPriceItems(),
                productDeal.getDiscount().getTotalFullPriceItems());*/
        assertNotNull(productDeal.getLastModifiedDate());
    }
}
