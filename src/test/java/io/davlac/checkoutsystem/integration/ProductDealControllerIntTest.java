package io.davlac.checkoutsystem.integration;

import io.davlac.checkoutsystem.config.JsonUtils;
import io.davlac.checkoutsystem.product.controller.ProductController;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.DiscountRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static io.davlac.checkoutsystem.config.JsonUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ProductController.class)
@AutoConfigureMockMvc
@EnableWebMvc
@ComponentScan("io.davlac.checkoutsystem")
class ProductDealControllerIntTest {

    private static final String PRODUCT_DEALS_URI = "/product-deals";
    private static final long PRODUCT_ID = 123L;
    private static final int DISCOUNT_50 = 50;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtils jsonUtils;

    @Test
    void create_withDirectDiscount_shouldReturnSavedDeal() throws Exception {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .withDiscount(
                        DiscountRequest.builder()
                                .withDiscountPercentage(DISCOUNT_50)
                                .withTotalDiscountedItems(1)
                                .withTotalFullPriceItems(0)
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
        assertEquals(request.getProductId(), response.getProductId());
        assertEquals(request.getDiscount().getDiscountPercentage(),
                response.getDiscount().getDiscountPercentage());
        assertEquals(request.getDiscount().getTotalDiscountedItems(),
                response.getDiscount().getTotalDiscountedItems());
        assertEquals(request.getDiscount().getTotalFullPriceItems(),
                response.getDiscount().getTotalFullPriceItems());
        assertNotNull(response.getLastModifiedDate());
    }
}
