package io.davlac.checkoutsystem.integration;

import io.davlac.checkoutsystem.basket.controller.BasketProductController;
import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.repository.BasketProductRepository;
import io.davlac.checkoutsystem.basket.service.dto.AddBasketProductRequest;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.repository.ProductRepository;
import io.davlac.checkoutsystem.utils.JsonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.stream.Stream;

import static io.davlac.checkoutsystem.utils.JsonUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private static final String NAME = "product_name";
    private static final String DESCRIPTION = "description";
    private static final double PRICE = 12.34;
    private static final String NAME_2 = "product_name-2";
    private static final String DESCRIPTION_2 = "description-2";
    private static final double PRICE_2 = 45.67;
    private static final long PRODUCT_ID = 123L;
    private static final int QUANTITY = 10;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private BasketProductRepository basketProductRepository;

    @Autowired
    private ProductRepository productRepository;

    Product savedProduct = new Product();
    Product savedProduct2 = new Product();

    @BeforeEach
    public void setUp() {
        Product product = new Product();
        product.setName(NAME);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);
        savedProduct = productRepository.save(product);

        Product product2 = new Product();
        product2.setName(NAME_2);
        product2.setDescription(DESCRIPTION_2);
        product2.setPrice(PRICE_2);
        savedProduct2 = productRepository.save(product2);
    }

    @AfterEach
    public void clean() {
        basketProductRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void addProduct_withEmptyBasket_shouldAddProductInBasket() throws Exception {
        int basketBefore = basketProductRepository.findAll().size();

        AddBasketProductRequest request = AddBasketProductRequest.builder()
                .withProductId(savedProduct.getId())
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

        // database assertions
        int basketAfter = basketProductRepository.findAll().size();
        assertEquals(basketBefore + 1, basketAfter);
        assertEquals(1, basketAfter);
    }

    @Test
    @Transactional
    void addProduct_withAlreadyFilledBasket_shouldAddQuantityProduct() throws Exception {
        int quantityBefore = 10;
        BasketProduct basketProductBefore = new BasketProduct(savedProduct, quantityBefore);
        basketProductRepository.save(basketProductBefore);
        int basketBefore = basketProductRepository.findAll().size();

        AddBasketProductRequest request = AddBasketProductRequest.builder()
                .withProductId(savedProduct.getId())
                .withQuantity(1)
                .build();

        ResultActions resultActions = mockMvc.perform(post(BASKET_PRODUCTS_URI + ADD_PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk());

        BasketProductResponse response = (BasketProductResponse) jsonUtils
                .deserializeResult(resultActions, BasketProductResponse.class);

        assertEquals(request.getProductId(), response.getProductId());
        assertEquals(quantityBefore + 1, response.getQuantity());
        assertNotNull(response.getLastModifiedDate());

        // database assertions
        int basketAfter = basketProductRepository.findAll().size();
        assertEquals(basketBefore, basketAfter);
        assertEquals(1, basketAfter);
    }

    @Test
    @Transactional
    void addProduct_withAlreadyFilledBasketAnd2ndProduct_shouldAddQuantityProductToTheRightOne() throws Exception {
        int quantityBefore = 5;
        BasketProduct basketProductBefore = new BasketProduct(savedProduct, quantityBefore);
        basketProductRepository.save(basketProductBefore);
        basketProductRepository.save(new BasketProduct(savedProduct2, 1));
        int basketBefore = basketProductRepository.findAll().size();

        AddBasketProductRequest request = AddBasketProductRequest.builder()
                .withProductId(savedProduct.getId())
                .withQuantity(1)
                .build();

        ResultActions resultActions = mockMvc.perform(post(BASKET_PRODUCTS_URI + ADD_PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk());

        BasketProductResponse response = (BasketProductResponse) jsonUtils
                .deserializeResult(resultActions, BasketProductResponse.class);

        assertEquals(request.getProductId(), response.getProductId());
        assertEquals(quantityBefore + 1, response.getQuantity());
        assertNotNull(response.getLastModifiedDate());

        // database assertions
        int basketAfter = basketProductRepository.findAll().size();
        assertEquals(basketBefore, basketAfter);
        assertEquals(2, basketAfter);
        assertEquals(quantityBefore + 1, basketProductRepository.findByProduct(savedProduct).get().getQuantity());
        assertEquals(1, basketProductRepository.findByProduct(savedProduct2).get().getQuantity());
    }

    public static Stream<Arguments> addProductBadRequestParameters() {
        return Stream.of(
                Arguments.of(
                        "Request empty",
                        AddBasketProductRequest.builder()
                                .build()
                ),
                Arguments.of(
                        "Product ID null",
                        AddBasketProductRequest.builder()
                                .withProductId(null)
                                .build()
                ),
                Arguments.of(
                        "With no quantity",
                        AddBasketProductRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .build()
                ),
                Arguments.of(
                        "With null quantity",
                        AddBasketProductRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withQuantity(null)
                                .build()
                ),
                Arguments.of(
                        "With quantity 0",
                        AddBasketProductRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withQuantity(0)
                                .build()
                ),
                Arguments.of(
                        "With quantity negative",
                        AddBasketProductRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withQuantity(-10)
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("addProductBadRequestParameters")
    void addProduct_withErrors_shouldThrowBadRequest(String test,
                                                     AddBasketProductRequest request) throws Exception {
        mockMvc.perform(post(BASKET_PRODUCTS_URI + ADD_PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void patchByProductId_withExistingProduct_shouldUpdateProductQuantity() throws Exception {
        BasketProduct basketProductSaved = basketProductRepository.save(
                new BasketProduct(savedProduct, 5)
        );

        ResultActions resultActions = mockMvc.perform(
                patch(BASKET_PRODUCTS_URI + "/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("quantity", String.valueOf(QUANTITY)))
                .andExpect(status().isOk());

        BasketProductResponse response = (BasketProductResponse) jsonUtils
                .deserializeResult(resultActions, BasketProductResponse.class);

        assertEquals(basketProductSaved.getProductId(), response.getProductId());
        assertEquals(QUANTITY, response.getQuantity());
        assertNotNull(response.getLastModifiedDate());

        // database assertions
        assertEquals(QUANTITY, basketProductRepository.findByProduct(savedProduct).get().getQuantity());
    }

    @Test
    void patchByProductId_withNotExistingBasketProduct_shouldThrowNotFoundError() throws Exception {
        mockMvc.perform(patch(BASKET_PRODUCTS_URI + "/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("quantity", String.valueOf(QUANTITY)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchByProductId_withNotExistingProduct_shouldThrowNotFoundError() throws Exception {
        mockMvc.perform(patch(BASKET_PRODUCTS_URI + "/456789")
                .contentType(MediaType.APPLICATION_JSON)
                .param("quantity", String.valueOf(QUANTITY)))
                .andExpect(status().isNotFound());
    }

    public static Stream<Arguments> patchByProductIdBadRequestParameters() {
        return Stream.of(
                Arguments.of("Quantity 0", PRODUCT_ID, 0),
                Arguments.of("Quantity negative", PRODUCT_ID, -10)
        );
    }

    @ParameterizedTest
    @MethodSource("patchByProductIdBadRequestParameters")
    void patchByProductId_withErrors_shouldThrowBadRequest(String test, long productId, int quantity) throws Exception {
        mockMvc.perform(
                patch(BASKET_PRODUCTS_URI + "/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isBadRequest());
    }
}
