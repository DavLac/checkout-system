package io.davlac.checkoutsystem.integration;

import io.davlac.checkoutsystem.basket.controller.BasketProductController;
import io.davlac.checkoutsystem.basket.service.dto.AddBasketProductRequest;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
import io.davlac.checkoutsystem.utils.JsonUtils;
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

import static io.davlac.checkoutsystem.utils.JsonUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BasketProductController.class)
@AutoConfigureMockMvc
@EnableWebMvc
@ComponentScan("io.davlac.checkoutsystem")
class BasketControllerIntTest {

    private static final String BASKET_PRODUCTS_URI = "/basket-products";
    private static final String ADD_PRODUCTS_URI = "/add";
    private static final long PRODUCT_ID = 123L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtils jsonUtils;

    @Test
    void addProduct_withExistingProduct_shouldAddProductInBasket() throws Exception {
        AddBasketProductRequest request = AddBasketProductRequest.builder()
                .withProductId(PRODUCT_ID)
                .withQuantity(1)
                .build();

        ResultActions resultActions = mockMvc.perform(post(BASKET_PRODUCTS_URI + ADD_PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk());

        BasketProductResponse response = (BasketProductResponse) jsonUtils
                .deserializeResult(resultActions, BasketProductResponse.class);

        assertEquals(request.getProductId(), response.getProductId());
        assertEquals(request.getQuantity(), response.getQuantity());
        assertNotNull(response.getLastModifiedDate());
    }
}
