package io.davlac.checkoutsystem.integration;

import io.davlac.checkoutsystem.basket.controller.BasketProductController;
import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.repository.BasketProductRepository;
import io.davlac.checkoutsystem.basket.service.dto.AddBasketProductRequest;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
import io.davlac.checkoutsystem.basket.service.dto.TotalBasketProductResponse;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.repository.ProductRepository;
import io.davlac.checkoutsystem.productdeal.model.Bundle;
import io.davlac.checkoutsystem.productdeal.model.Discount;
import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import io.davlac.checkoutsystem.productdeal.repository.ProductDealRepository;
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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.davlac.checkoutsystem.utils.JsonUtils.asJsonString;
import static io.davlac.checkoutsystem.utils.NumbersUtils.roundUpBy2Decimals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BasketProductController.class)
@AutoConfigureMockMvc
@EnableWebMvc
@Transactional
@ComponentScan("io.davlac.checkoutsystem")
class BasketControllerIntTest {

    private static final String BASKET_PRODUCTS_URI = "/basket-products";
    private static final String ADD_PRODUCTS_URI = "/add";
    private static final String CALCULATE_TOTAL_PRODUCTS_URI = "/calculate-total";
    private static final String PRODUCTS_URI = "/products";
    private static final String NAME = "product_name";
    private static final String DESCRIPTION = "description";
    private static final double PRICE = 12.34;
    private static final double PRICE_10 = 10;
    private static final String NAME_2 = "product_name-2";
    private static final String DESCRIPTION_2 = "description-2";
    private static final double PRICE_2 = 45.67;
    private static final long PRODUCT_ID = 123L;
    private static final int QUANTITY_10 = 10;
    private static final int QUANTITY_5 = 5;
    private static final int QUANTITY_2 = 2;
    private static final int QUANTITY_1 = 1;
    private static final int PERCENT_70 = 70;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private BasketProductRepository basketProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDealRepository productDealRepository;

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
    void patchByProductId_withExistingProduct_shouldUpdateProductQuantity() throws Exception {
        BasketProduct basketProductSaved = basketProductRepository.save(
                new BasketProduct(savedProduct, 5)
        );

        ResultActions resultActions = mockMvc.perform(
                patch(BASKET_PRODUCTS_URI + PRODUCTS_URI + "/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("quantity", String.valueOf(QUANTITY_10)))
                .andExpect(status().isOk());

        BasketProductResponse response = (BasketProductResponse) jsonUtils
                .deserializeResult(resultActions, BasketProductResponse.class);

        assertEquals(basketProductSaved.getProductId(), response.getProductId());
        assertEquals(QUANTITY_10, response.getQuantity());
        assertNotNull(response.getLastModifiedDate());

        // database assertions
        assertEquals(QUANTITY_10, basketProductRepository.findByProduct(savedProduct).get().getQuantity());
    }

    @Test
    void patchByProductId_withNotExistingBasketProduct_shouldThrowNotFoundError() throws Exception {
        mockMvc.perform(patch(BASKET_PRODUCTS_URI + PRODUCTS_URI + "/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("quantity", String.valueOf(QUANTITY_10)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchByProductId_withNotExistingProduct_shouldThrowNotFoundError() throws Exception {
        mockMvc.perform(patch(BASKET_PRODUCTS_URI + PRODUCTS_URI + "/456789")
                .contentType(MediaType.APPLICATION_JSON)
                .param("quantity", String.valueOf(QUANTITY_10)))
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
                patch(BASKET_PRODUCTS_URI + PRODUCTS_URI + "/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteByProductId_withExistingProduct_shouldDeleteBasketProduct() throws Exception {
        basketProductRepository.save(new BasketProduct(savedProduct, 5));
        int basketBefore = basketProductRepository.findAll().size();

        mockMvc.perform(delete(BASKET_PRODUCTS_URI + PRODUCTS_URI + "/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // database assertions
        int basketAfter = basketProductRepository.findAll().size();
        assertEquals(basketBefore - 1, basketAfter);
        assertEquals(0, basketAfter);
        assertEquals(Optional.empty(), basketProductRepository.findByProduct(savedProduct));
    }

    @Test
    void deleteByProductId_withNotExistingBasketProduct_shouldThrowNotFoundError() throws Exception {
        mockMvc.perform(delete(BASKET_PRODUCTS_URI + PRODUCTS_URI + "/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteByProductId_withNotExistingProduct_shouldThrowNotFoundError() throws Exception {
        mockMvc.perform(delete(BASKET_PRODUCTS_URI + PRODUCTS_URI + "/456789")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void calculateTotalPrice_withExistingProductBasketsAndNoDeals_shouldReturnTotalPriceAndProductDetails() throws Exception {
        basketProductRepository.save(new BasketProduct(savedProduct, QUANTITY_5));

        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        assertEquals(PRICE * QUANTITY_5, response.getTotalPrice());
        assertEquals(1, response.getProductDetails().size());
        assertEquals(savedProduct.getId(), response.getProductDetails().get(0).getProductId());
        assertEquals(QUANTITY_5, response.getProductDetails().get(0).getQuantity());
        assertEquals(PRICE * QUANTITY_5, response.getProductDetails().get(0).getProductTotalPriceBeforeDiscounts());
        assertEquals(PRICE * QUANTITY_5, response.getProductDetails().get(0).getProductTotalPriceAfterDiscount());
        assertEquals(0, response.getProductDetails().get(0).getProductDeals().size());
    }

    @Test
    void calculateTotalPrice_with2ProductBasketsAndNoDeals_shouldReturnTotalPriceAndProductDetails() throws Exception {
        basketProductRepository.save(new BasketProduct(savedProduct, QUANTITY_5));
        basketProductRepository.save(new BasketProduct(savedProduct2, QUANTITY_2));

        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        double product1Total = PRICE * QUANTITY_5;
        double product2Total = PRICE_2 * QUANTITY_2;
        assertEquals(roundUpBy2Decimals(product1Total + product2Total), response.getTotalPrice());
        assertEquals(2, response.getProductDetails().size());

        assertEquals(savedProduct.getId(), response.getProductDetails().get(0).getProductId());
        assertEquals(QUANTITY_5, response.getProductDetails().get(0).getQuantity());
        assertEquals(product1Total, response.getProductDetails().get(0).getProductTotalPriceBeforeDiscounts());
        assertEquals(product1Total, response.getProductDetails().get(0).getProductTotalPriceAfterDiscount());
        assertEquals(0, response.getProductDetails().get(0).getProductDeals().size());

        assertEquals(savedProduct2.getId(), response.getProductDetails().get(1).getProductId());
        assertEquals(QUANTITY_2, response.getProductDetails().get(1).getQuantity());
        assertEquals(product2Total, response.getProductDetails().get(1).getProductTotalPriceBeforeDiscounts());
        assertEquals(product2Total, response.getProductDetails().get(1).getProductTotalPriceAfterDiscount());
        assertEquals(0, response.getProductDetails().get(1).getProductDeals().size());
    }

    @Test
    void calculateTotalPrice_with1ProductBasketsAndDeals_shouldReturnTotalPriceDiscounted() throws Exception {
        // init database
        savedProduct.setPrice(PRICE_10);
        basketProductRepository.save(new BasketProduct(savedProduct, QUANTITY_5));

        // create a product deal
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        // product 70% discount
        productDeal.setDiscount(new Discount(0, 1, PERCENT_70));
        ProductDeal productDealSaved = productDealRepository.save(productDeal);

        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        double productTotal = PRICE_10 * QUANTITY_5;
        double productTotalDiscount = productTotal * (100 - PERCENT_70) / 100;
        assertEquals(productTotalDiscount, response.getTotalPrice());
        assertEquals(1, response.getProductDetails().size());
        assertEquals(savedProduct.getId(), response.getProductDetails().get(0).getProductId());
        assertEquals(QUANTITY_5, response.getProductDetails().get(0).getQuantity());
        assertEquals(productTotal, response.getProductDetails().get(0).getProductTotalPriceBeforeDiscounts());
        assertEquals(productTotalDiscount, response.getProductDetails().get(0).getProductTotalPriceAfterDiscount());
        assertEquals(1, response.getProductDetails().get(0).getProductDeals().size());
        assertEquals(productDealSaved.getId(), response.getProductDetails().get(0).getProductDeals().get(0).getId());
    }

    @Test
    void calculateTotalPrice_with1ProductBuy5And2Free_shouldReturnTotalPriceDiscounted() throws Exception {
        // init database
        savedProduct.setPrice(PRICE_10);
        basketProductRepository.save(new BasketProduct(savedProduct, QUANTITY_5));

        // create a product deal
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        // buy 5, 2 free
        productDeal.setDiscount(new Discount(3, 2, 100));
        ProductDeal productDealSaved = productDealRepository.save(productDeal);

        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        double productTotal = PRICE_10 * QUANTITY_5;
        double productTotalDiscount = PRICE_10 * 3;
        assertEquals(productTotalDiscount, response.getTotalPrice());
        assertEquals(1, response.getProductDetails().size());
        assertEquals(savedProduct.getId(), response.getProductDetails().get(0).getProductId());
        assertEquals(QUANTITY_5, response.getProductDetails().get(0).getQuantity());
        assertEquals(productTotal, response.getProductDetails().get(0).getProductTotalPriceBeforeDiscounts());
        assertEquals(productTotalDiscount, response.getProductDetails().get(0).getProductTotalPriceAfterDiscount());
        assertEquals(1, response.getProductDetails().get(0).getProductDeals().size());
        assertEquals(productDealSaved.getId(), response.getProductDetails().get(0).getProductDeals().get(0).getId());
    }

    @Test
    void calculateTotalPrice_with1ProductBuy6And2FreeNotEnoughQuantity_shouldNotApplyDiscount() throws Exception {
        // init database
        savedProduct.setPrice(PRICE_10);
        basketProductRepository.save(new BasketProduct(savedProduct, QUANTITY_5));

        // create a product deal
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        // buy 6, 2 free
        productDeal.setDiscount(new Discount(4, 2, 100));
        ProductDeal productDealSaved = productDealRepository.save(productDeal);

        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        double productTotal = PRICE_10 * QUANTITY_5;
        assertEquals(productTotal, response.getTotalPrice());
        assertEquals(1, response.getProductDetails().size());
        assertEquals(savedProduct.getId(), response.getProductDetails().get(0).getProductId());
        assertEquals(QUANTITY_5, response.getProductDetails().get(0).getQuantity());
        assertEquals(productTotal, response.getProductDetails().get(0).getProductTotalPriceBeforeDiscounts());
        assertEquals(productTotal, response.getProductDetails().get(0).getProductTotalPriceAfterDiscount());
        assertEquals(1, response.getProductDetails().get(0).getProductDeals().size());
        assertEquals(productDealSaved.getId(), response.getProductDetails().get(0).getProductDeals().get(0).getId());
    }

    @Test
    void calculateTotalPrice_withBundleBuy1AnotherFree_shouldApplyBundle() throws Exception {
        // init database
        savedProduct.setPrice(PRICE_10);
        savedProduct2.setPrice(PRICE_2);
        basketProductRepository.save(new BasketProduct(savedProduct, QUANTITY_1));
        basketProductRepository.save(new BasketProduct(savedProduct2, QUANTITY_1));

        // create a product deal
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        // buy 1 product, product2 free
        productDeal.setBundles(Set.of(
                new Bundle(savedProduct2, 100, productDeal)
        ));
        ProductDeal productDealSaved = productDealRepository.save(productDeal);

        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        double productTotal = PRICE_10;
        assertEquals(productTotal, response.getTotalPrice());
        assertEquals(2, response.getProductDetails().size());

        assertEquals(savedProduct.getId(), response.getProductDetails().get(0).getProductId());
        assertEquals(QUANTITY_1, response.getProductDetails().get(0).getQuantity());
        assertEquals(productTotal, response.getProductDetails().get(0).getProductTotalPriceBeforeDiscounts());
        assertEquals(productTotal, response.getProductDetails().get(0).getProductTotalPriceAfterDiscount());
        assertEquals(productTotal, response.getProductDetails().get(0).getProductTotalPriceAfterBundle());
        assertEquals(1, response.getProductDetails().get(0).getProductDeals().size());
        assertEquals(productDealSaved.getId(), response.getProductDetails().get(0).getProductDeals().get(0).getId());

        assertEquals(savedProduct2.getId(), response.getProductDetails().get(1).getProductId());
        assertEquals(QUANTITY_1, response.getProductDetails().get(1).getQuantity());
        assertEquals(PRICE_2, response.getProductDetails().get(1).getProductTotalPriceBeforeDiscounts());
        assertEquals(PRICE_2, response.getProductDetails().get(1).getProductTotalPriceAfterDiscount());
        assertEquals(0, response.getProductDetails().get(1).getProductTotalPriceAfterBundle());
        assertEquals(0, response.getProductDetails().get(1).getProductDeals().size());
    }

    @Test
    void calculateTotalPrice_withBundleWith2Products_shouldApplyBundle2Times() throws Exception {
        // init database
        savedProduct.setPrice(PRICE_10);
        savedProduct2.setPrice(PRICE_2);
        basketProductRepository.save(new BasketProduct(savedProduct, QUANTITY_2));
        basketProductRepository.save(new BasketProduct(savedProduct2, QUANTITY_5));

        // create a product deal
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        // buy 1 product, product2 50%
        productDeal.setBundles(Set.of(
                new Bundle(savedProduct2, 50, productDeal)
        ));
        ProductDeal productDealSaved = productDealRepository.save(productDeal);

        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        // product1 full price *2 + product2 full price *3 + 50% *2
        double productTotal = PRICE_10 * QUANTITY_2 + PRICE_2 * 3 + (PRICE_2 * QUANTITY_2) / 2;
        assertEquals(productTotal, response.getTotalPrice());
        assertEquals(2, response.getProductDetails().size());

        assertEquals(savedProduct.getId(), response.getProductDetails().get(0).getProductId());
        assertEquals(QUANTITY_2, response.getProductDetails().get(0).getQuantity());
        assertEquals(PRICE_10 * QUANTITY_2, response.getProductDetails().get(0).getProductTotalPriceBeforeDiscounts());
        assertEquals(PRICE_10 * QUANTITY_2, response.getProductDetails().get(0).getProductTotalPriceAfterDiscount());
        assertEquals(PRICE_10 * QUANTITY_2, response.getProductDetails().get(0).getProductTotalPriceAfterBundle());
        assertEquals(1, response.getProductDetails().get(0).getProductDeals().size());
        assertEquals(productDealSaved.getId(), response.getProductDetails().get(0).getProductDeals().get(0).getId());

        assertEquals(savedProduct2.getId(), response.getProductDetails().get(1).getProductId());
        assertEquals(QUANTITY_5, response.getProductDetails().get(1).getQuantity());
        assertEquals(roundUpBy2Decimals(PRICE_2 * QUANTITY_5), response.getProductDetails().get(1).getProductTotalPriceBeforeDiscounts());
        assertEquals(roundUpBy2Decimals(PRICE_2 * QUANTITY_5), response.getProductDetails().get(1).getProductTotalPriceAfterDiscount());
        assertEquals(roundUpBy2Decimals(PRICE_2 * 3 + (PRICE_2 * QUANTITY_2) / 2), response.getProductDetails().get(1).getProductTotalPriceAfterBundle());
        assertEquals(0, response.getProductDetails().get(1).getProductDeals().size());
    }

    @Test
    void calculateTotalPrice_withBundleWithoutOtherProduct_shouldNotApplyBundle() throws Exception {
        // init database
        savedProduct.setPrice(PRICE_10);
        basketProductRepository.save(new BasketProduct(savedProduct, QUANTITY_2));

        // create a product deal
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        // buy 1 product, product2 50%
        productDeal.setBundles(Set.of(
                new Bundle(savedProduct2, 50, productDeal)
        ));
        ProductDeal productDealSaved = productDealRepository.save(productDeal);

        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        double productTotal = PRICE_10 * QUANTITY_2;
        assertEquals(productTotal, response.getTotalPrice());
        assertEquals(1, response.getProductDetails().size());

        assertEquals(savedProduct.getId(), response.getProductDetails().get(0).getProductId());
        assertEquals(QUANTITY_2, response.getProductDetails().get(0).getQuantity());
        assertEquals(productTotal, response.getProductDetails().get(0).getProductTotalPriceBeforeDiscounts());
        assertEquals(productTotal, response.getProductDetails().get(0).getProductTotalPriceAfterDiscount());
        assertEquals(productTotal, response.getProductDetails().get(0).getProductTotalPriceAfterBundle());
        assertEquals(1, response.getProductDetails().get(0).getProductDeals().size());
        assertEquals(productDealSaved.getId(), response.getProductDetails().get(0).getProductDeals().get(0).getId());
    }

    @Test
    void calculateTotalPrice_withBundleBiggerDiscountThanDiscount_shouldApplyOnlyBundle() throws Exception {
        // init database
        savedProduct.setPrice(PRICE_10);
        savedProduct2.setPrice(PRICE_2);
        basketProductRepository.save(new BasketProduct(savedProduct, QUANTITY_1));
        basketProductRepository.save(new BasketProduct(savedProduct2, QUANTITY_1));

        // create a product deal
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        // buy 1 product, product2 50%
        productDeal.setBundles(Set.of(
                new Bundle(savedProduct2, 50, productDeal)
        ));
        ProductDeal productDealSaved = productDealRepository.save(productDeal);

        ProductDeal productDeal2 = new ProductDeal();
        productDeal2.setProduct(savedProduct2);
        // direct discount 25%
        productDeal2.setDiscount(new Discount(0, 1, 25));
        productDealRepository.save(productDeal2);

        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        // product1 full price + product2 50%
        double productTotal = PRICE_10 * QUANTITY_1 + PRICE_2 * QUANTITY_1 / 2;
        assertEquals(roundUpBy2Decimals(productTotal), response.getTotalPrice());
        assertEquals(2, response.getProductDetails().size());

        assertEquals(savedProduct.getId(), response.getProductDetails().get(0).getProductId());
        assertEquals(QUANTITY_1, response.getProductDetails().get(0).getQuantity());
        assertEquals(PRICE_10 * QUANTITY_1, response.getProductDetails().get(0).getProductTotalPriceBeforeDiscounts());
        assertEquals(PRICE_10 * QUANTITY_1, response.getProductDetails().get(0).getProductTotalPriceAfterDiscount());
        assertEquals(PRICE_10 * QUANTITY_1, response.getProductDetails().get(0).getProductTotalPriceAfterBundle());
        assertEquals(1, response.getProductDetails().get(0).getProductDeals().size());
        assertEquals(productDealSaved.getId(), response.getProductDetails().get(0).getProductDeals().get(0).getId());
        assertNotNull(response.getProductDetails().get(0).getProductDeals().get(0).getBundles());

        assertEquals(savedProduct2.getId(), response.getProductDetails().get(1).getProductId());
        assertEquals(QUANTITY_1, response.getProductDetails().get(1).getQuantity());
        assertEquals(roundUpBy2Decimals(PRICE_2 * QUANTITY_1), response.getProductDetails().get(1).getProductTotalPriceBeforeDiscounts());
        assertEquals(roundUpBy2Decimals(PRICE_2 * QUANTITY_1 * 0.75), response.getProductDetails().get(1).getProductTotalPriceAfterDiscount());
        assertEquals(roundUpBy2Decimals(PRICE_2 * QUANTITY_1 / 2), response.getProductDetails().get(1).getProductTotalPriceAfterBundle());
        assertEquals(1, response.getProductDetails().get(1).getProductDeals().size());
        assertNotNull(response.getProductDetails().get(1).getProductDeals().get(0).getDiscount());
    }

    @Test
    void calculateTotalPrice_withBundleWith3Products_shouldApplyGoodDiscountPercentageByProduct() throws Exception {
        // init database
        savedProduct.setPrice(PRICE_10);
        savedProduct2.setPrice(PRICE_2);
        basketProductRepository.save(new BasketProduct(savedProduct, QUANTITY_1));
        basketProductRepository.save(new BasketProduct(savedProduct2, QUANTITY_1));

        // create a product deal
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        // buy 1 product, product2 50%
        productDeal.setBundles(Set.of(
                new Bundle(savedProduct2, 50, productDeal)
        ));
        ProductDeal productDealSaved = productDealRepository.save(productDeal);

        ProductDeal productDeal2 = new ProductDeal();
        productDeal2.setProduct(savedProduct2);
        // direct discount 25%
        productDeal2.setDiscount(new Discount(0, 1, 25));
        productDealRepository.save(productDeal2);

        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        // product1 full price + product2 50%
        double productTotal = PRICE_10 * QUANTITY_1 + PRICE_2 * QUANTITY_1 / 2;
        assertEquals(roundUpBy2Decimals(productTotal), response.getTotalPrice());
        assertEquals(2, response.getProductDetails().size());

        assertEquals(savedProduct.getId(), response.getProductDetails().get(0).getProductId());
        assertEquals(QUANTITY_1, response.getProductDetails().get(0).getQuantity());
        assertEquals(PRICE_10 * QUANTITY_1, response.getProductDetails().get(0).getProductTotalPriceBeforeDiscounts());
        assertEquals(PRICE_10 * QUANTITY_1, response.getProductDetails().get(0).getProductTotalPriceAfterDiscount());
        assertEquals(PRICE_10 * QUANTITY_1, response.getProductDetails().get(0).getProductTotalPriceAfterBundle());
        assertEquals(1, response.getProductDetails().get(0).getProductDeals().size());
        assertEquals(productDealSaved.getId(), response.getProductDetails().get(0).getProductDeals().get(0).getId());
        assertNotNull(response.getProductDetails().get(0).getProductDeals().get(0).getBundles());

        assertEquals(savedProduct2.getId(), response.getProductDetails().get(1).getProductId());
        assertEquals(QUANTITY_1, response.getProductDetails().get(1).getQuantity());
        assertEquals(roundUpBy2Decimals(PRICE_2 * QUANTITY_1), response.getProductDetails().get(1).getProductTotalPriceBeforeDiscounts());
        assertEquals(roundUpBy2Decimals(PRICE_2 * QUANTITY_1 * 0.75), response.getProductDetails().get(1).getProductTotalPriceAfterDiscount());
        assertEquals(roundUpBy2Decimals(PRICE_2 * QUANTITY_1 / 2), response.getProductDetails().get(1).getProductTotalPriceAfterBundle());
        assertEquals(1, response.getProductDetails().get(1).getProductDeals().size());
        assertNotNull(response.getProductDetails().get(1).getProductDeals().get(0).getDiscount());
    }

    @Test
    void calculateTotalPrice_withDiscountBiggerDiscountThanBundle_shouldApplyOnlyDiscount() throws Exception {
        // init database
        savedProduct.setPrice(PRICE_10);
        savedProduct2.setPrice(PRICE_2);
        Product product3 = new Product();
        product3.setName(NAME_2);
        product3.setPrice(PRICE);
        Product savedProduct3 = productRepository.save(product3);
        basketProductRepository.save(new BasketProduct(savedProduct, QUANTITY_2));
        basketProductRepository.save(new BasketProduct(savedProduct2, QUANTITY_2));
        basketProductRepository.save(new BasketProduct(savedProduct3, QUANTITY_5));

        // create a product deal
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        // buy 1 product, product2 50%, product3 25%
        productDeal.setBundles(Set.of(
                new Bundle(savedProduct2, 50, productDeal),
                new Bundle(savedProduct3, 25, productDeal)
        ));
        ProductDeal productDealSaved = productDealRepository.save(productDeal);

        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        // product1 full price + product2 50% + product3 25% (2)
        double productTotal = PRICE_10 * QUANTITY_2 +
                PRICE_2 * QUANTITY_2 / 2 +
                PRICE * QUANTITY_2 * 0.75 + PRICE * 3;
        assertEquals(roundUpBy2Decimals(productTotal), response.getTotalPrice());
        assertEquals(3, response.getProductDetails().size());

        assertEquals(savedProduct.getId(), response.getProductDetails().get(0).getProductId());
        assertEquals(QUANTITY_2, response.getProductDetails().get(0).getQuantity());
        assertEquals(PRICE_10 * QUANTITY_2, response.getProductDetails().get(0).getProductTotalPriceBeforeDiscounts());
        assertEquals(PRICE_10 * QUANTITY_2, response.getProductDetails().get(0).getProductTotalPriceAfterDiscount());
        assertEquals(PRICE_10 * QUANTITY_2, response.getProductDetails().get(0).getProductTotalPriceAfterBundle());
        assertEquals(1, response.getProductDetails().get(0).getProductDeals().size());
        assertEquals(productDealSaved.getId(), response.getProductDetails().get(0).getProductDeals().get(0).getId());
        assertNotNull(response.getProductDetails().get(0).getProductDeals().get(0).getBundles());
        assertNull(response.getProductDetails().get(0).getProductDeals().get(0).getDiscount());

        assertEquals(savedProduct2.getId(), response.getProductDetails().get(1).getProductId());
        assertEquals(QUANTITY_2, response.getProductDetails().get(1).getQuantity());
        assertEquals(roundUpBy2Decimals(PRICE_2 * QUANTITY_2), response.getProductDetails().get(1).getProductTotalPriceBeforeDiscounts());
        assertEquals(roundUpBy2Decimals(PRICE_2 * QUANTITY_2), response.getProductDetails().get(1).getProductTotalPriceAfterDiscount());
        assertEquals(roundUpBy2Decimals(PRICE_2 * QUANTITY_2 / 2), response.getProductDetails().get(1).getProductTotalPriceAfterBundle());
        assertEquals(0, response.getProductDetails().get(1).getProductDeals().size());

        assertEquals(savedProduct3.getId(), response.getProductDetails().get(2).getProductId());
        assertEquals(QUANTITY_5, response.getProductDetails().get(2).getQuantity());
        assertEquals(roundUpBy2Decimals(PRICE * QUANTITY_5), response.getProductDetails().get(2).getProductTotalPriceBeforeDiscounts());
        assertEquals(roundUpBy2Decimals(PRICE * QUANTITY_5), response.getProductDetails().get(2).getProductTotalPriceAfterDiscount());
        assertEquals(roundUpBy2Decimals(PRICE * QUANTITY_2 * 0.75 + PRICE * 3), response.getProductDetails().get(2).getProductTotalPriceAfterBundle());
        assertEquals(0, response.getProductDetails().get(2).getProductDeals().size());
    }

    @Test
    void calculateTotalPrice_withNoProducts_shouldReturnTotalPriceZero() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                post(BASKET_PRODUCTS_URI + CALCULATE_TOTAL_PRODUCTS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        TotalBasketProductResponse response = (TotalBasketProductResponse) jsonUtils
                .deserializeResult(resultActions, TotalBasketProductResponse.class);

        assertEquals(0, response.getTotalPrice());
        assertEquals(0, response.getProductDetails().size());
    }
}
