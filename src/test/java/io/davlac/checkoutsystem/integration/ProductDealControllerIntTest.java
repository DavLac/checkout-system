package io.davlac.checkoutsystem.integration;

import io.davlac.checkoutsystem.CheckoutSystemApplication;
import io.davlac.checkoutsystem.integration.repository.BundleRepository;
import io.davlac.checkoutsystem.integration.repository.DiscountRepository;
import io.davlac.checkoutsystem.integration.request.BundleRequestTest;
import io.davlac.checkoutsystem.integration.request.CreateProductDealRequestTest;
import io.davlac.checkoutsystem.integration.request.DiscountRequestTest;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.repository.ProductRepository;
import io.davlac.checkoutsystem.productdeal.model.Bundle;
import io.davlac.checkoutsystem.productdeal.model.Discount;
import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import io.davlac.checkoutsystem.productdeal.repository.ProductDealRepository;
import io.davlac.checkoutsystem.productdeal.service.dto.request.BundleRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.DiscountRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.davlac.checkoutsystem.utils.JsonUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CheckoutSystemApplication.class)
@AutoConfigureMockMvc
@EnableWebMvc
@ComponentScan("io.davlac.checkoutsystem")
class ProductDealControllerIntTest {

    private static final String PRODUCT_DEALS_URI = "/product-deals";
    private static final long PRODUCT_ID = 123L;
    private static final long PRODUCT_ID_2 = 456;
    private static final String NAME = "product_name";
    private static final String DESCRIPTION = "description";
    private static final double PRICE = 12.34;
    private static final String NAME_2 = "product_name-2";
    private static final String DESCRIPTION_2 = "description-2";
    private static final double PRICE_2 = 45.67;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private ProductDealRepository productDealRepository;

    @Autowired
    private BundleRepository bundleRepository;

    @Autowired
    private DiscountRepository discountRepository;

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
        productDealRepository.deleteAll();
        productRepository.deleteAll();
    }

    public static Stream<Arguments> createDealParameters() {
        return Stream.of(
                Arguments.of("50% discount on product id",
                        CreateProductDealRequestTest.builder()
                                .discount(
                                        DiscountRequestTest.builder()
                                                .discountPercentage(50)
                                                .totalDiscountedItems(1)
                                                .totalFullPriceItems(0)
                                                .build()
                                )
                                .build()),
                Arguments.of("Buy 2, third 70%",
                        CreateProductDealRequestTest.builder()
                                .discount(
                                        DiscountRequestTest.builder()
                                                .discountPercentage(70)
                                                .totalDiscountedItems(1)
                                                .totalFullPriceItems(2)
                                                .build()
                                )
                                .build()),
                Arguments.of("Buy 1 product, another free",
                        CreateProductDealRequestTest.builder()
                                .bundles(
                                        Set.of(
                                                new BundleRequestTest(100)
                                        )
                                )
                                .build()),
                Arguments.of("Buy 1 product, 2 other products 50%",
                        CreateProductDealRequestTest.builder()
                                .bundles(
                                        Set.of(
                                                new BundleRequestTest(50),
                                                new BundleRequestTest(50)
                                        )
                                )
                                .build()),
                Arguments.of("Deal with empty bundle",
                        CreateProductDealRequestTest.builder()
                                .discount(
                                        DiscountRequestTest.builder()
                                                .discountPercentage(70)
                                                .totalDiscountedItems(1)
                                                .totalFullPriceItems(2)
                                                .build()
                                )
                                .bundles(Set.of())
                                .build())
        );
    }

    @ParameterizedTest
    @MethodSource("createDealParameters")
    void create_withGoodData_shouldReturnSavedDeal(String test,
                                                   CreateProductDealRequestTest reqTemp) throws Exception {
        int dealsBefore = productDealRepository.findAll().size();

        // build bundle create request
        Set<BundleRequest> bundleRequests = new HashSet<>();
        if (!CollectionUtils.isEmpty(reqTemp.getBundles())) {
            reqTemp.getBundles().forEach(reqBundle -> {
                bundleRequests.add(BundleRequest.builder()
                        .withProductId(savedProduct2.getId())
                        .withDiscountPercentage(reqBundle.getDiscountPercentage())
                        .build());
            });
        }

        // build deal create request
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(savedProduct.getId())
                .withDiscount(
                        (reqTemp.getDiscount() != null)
                                ? DiscountRequest.builder()
                                .withDiscountPercentage(reqTemp.getDiscount().getDiscountPercentage())
                                .withTotalDiscountedItems(reqTemp.getDiscount().getTotalDiscountedItems())
                                .withTotalFullPriceItems(reqTemp.getDiscount().getTotalFullPriceItems())
                                .build()
                                : null
                )
                .withBundles(
                        (!CollectionUtils.isEmpty(reqTemp.getBundles()))
                                ? bundleRequests
                                : null
                )
                .build();

        // make request
        ResultActions resultActions = mockMvc.perform(post(PRODUCT_DEALS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());

        ProductDealResponse response = jsonUtils.deserializeResult(resultActions, ProductDealResponse.class);

        assertNotNull(response.getId());
        assertNotNull(response.getLastModifiedDate());

        // assertions product
        assertEquals(request.getProductId(), response.getProductId());

        // assertions discount
        if (request.getDiscount() != null) {
            assertNotNull(response.getDiscount().getId());
            assertEquals(request.getDiscount().getDiscountPercentage(), response.getDiscount().getDiscountPercentage());
            assertEquals(request.getDiscount().getTotalDiscountedItems(), response.getDiscount().getTotalDiscountedItems());
            assertEquals(request.getDiscount().getTotalFullPriceItems(), response.getDiscount().getTotalFullPriceItems());
            assertNotNull(response.getDiscount().getLastModifiedDate());
        }

        // assertions bundle
        if (!CollectionUtils.isEmpty(request.getBundles())) {
            assertNotNull(response.getBundles());
            assertEquals(request.getBundles().size(), response.getBundles().size());
            response.getBundles().forEach(responseBundle -> {
                assertNotNull(responseBundle.getId());
                assertNotNull(responseBundle.getLastModifiedDate());

                assertTrue(request.getBundles().stream()
                        .anyMatch(reqBundle -> reqBundle.getProductId().equals(responseBundle.getProductId())));
                assertTrue(request.getBundles().stream()
                        .anyMatch(reqBundle -> reqBundle.getDiscountPercentage().equals(responseBundle.getDiscountPercentage())));
            });
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
                                                .withDiscountPercentage(50)
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
                                .build()),
                Arguments.of("Bundle without product ID",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withBundles(
                                        Set.of(
                                                BundleRequest.builder()
                                                        .withDiscountPercentage(50)
                                                        .build()
                                        )
                                )
                                .build()),
                Arguments.of("Bundle without percentage discount",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withBundles(
                                        Set.of(
                                                BundleRequest.builder()
                                                        .withProductId(PRODUCT_ID_2)
                                                        .build()
                                        )
                                )
                                .build()),
                Arguments.of("Bundle with discount percentage negative",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withBundles(
                                        Set.of(
                                                BundleRequest.builder()
                                                        .withProductId(PRODUCT_ID_2)
                                                        .withDiscountPercentage(-10)
                                                        .build()
                                        )
                                )
                                .build()),
                Arguments.of("Bundle with discount percentage greater than 100",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withBundles(
                                        Set.of(
                                                BundleRequest.builder()
                                                        .withProductId(PRODUCT_ID_2)
                                                        .withDiscountPercentage(101)
                                                        .build()
                                        )
                                )
                                .build()),
                Arguments.of("Deal without discount and bundle",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .build()),
                Arguments.of("Bundle product same than targeted product",
                        CreateProductDealRequest.builder()
                                .withProductId(PRODUCT_ID)
                                .withBundles(
                                        Set.of(
                                                BundleRequest.builder()
                                                        .withProductId(PRODUCT_ID)
                                                        .withDiscountPercentage(101)
                                                        .build()
                                        )
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

    @Test
    void create_withAlreadyExistingDiscount_shouldThrowBadRequest() throws Exception {
        // create product deals
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        productDeal.setDiscount(new Discount(1, 1, 50));
        productDealRepository.save(productDeal);

        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(savedProduct.getId())
                .withDiscount(
                        DiscountRequest.builder()
                                .withDiscountPercentage(0)
                                .withTotalFullPriceItems(3)
                                .withTotalDiscountedItems(3)
                                .build()
                )
                .build();

        mockMvc.perform(post(PRODUCT_DEALS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withAlreadyExistingBundle_shouldThrowBadRequest() throws Exception {
        // create product deals
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        Bundle bundle = new Bundle();
        bundle.setProductDeal(productDeal);
        bundle.setProduct(savedProduct2);
        bundle.setDiscountPercentage(0);
        productDeal.setBundles(Set.of(bundle));
        productDealRepository.save(productDeal);

        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(savedProduct.getId())
                .withBundles(
                        Set.of(
                                BundleRequest.builder()
                                        .withProductId(savedProduct2.getId())
                                        .withDiscountPercentage(100)
                                        .build()
                        )
                )
                .build();

        mockMvc.perform(post(PRODUCT_DEALS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withNotExistingProduct_shouldThrowBadRequest() throws Exception {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .withBundles(
                        Set.of(
                                BundleRequest.builder()
                                        .withProductId(PRODUCT_ID_2)
                                        .withDiscountPercentage(100)
                                        .build()
                        )
                )
                .build();

        mockMvc.perform(post(PRODUCT_DEALS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withNotExistingProductBundle_shouldReturnBadRequest() throws Exception {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(savedProduct.getId())
                .withBundles(
                        Set.of(
                                BundleRequest.builder()
                                        .withProductId(PRODUCT_ID_2)
                                        .withDiscountPercentage(100)
                                        .build()
                        )
                )
                .build();

        mockMvc.perform(post(PRODUCT_DEALS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withDiscountAndBundleEmpty_shouldThrowBadRequest() throws Exception {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(savedProduct.getId())
                .build();

        mockMvc.perform(post(PRODUCT_DEALS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withSameProductDealAndBundle_shouldThrowBadRequest() throws Exception {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(savedProduct.getId())
                .withBundles(
                        Set.of(
                                BundleRequest.builder()
                                        .withProductId(savedProduct.getId())
                                        .withDiscountPercentage(100)
                                        .build()
                        )
                )
                .build();

        mockMvc.perform(post(PRODUCT_DEALS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById_withExistingProductDeal_shouldDeleteProductDealAndLinkedBundlesAndDiscount() throws Exception {
        // create a product deal
        ProductDeal productDeal = new ProductDeal();
        productDeal.setProduct(savedProduct);
        Bundle bundle = new Bundle();
        bundle.setProductDeal(productDeal);
        bundle.setProduct(savedProduct);
        bundle.setDiscountPercentage(0);
        Bundle bundle2 = new Bundle();
        bundle2.setProductDeal(productDeal);
        bundle2.setProduct(savedProduct2);
        bundle2.setDiscountPercentage(0);
        productDeal.setBundles(Set.of(bundle, bundle2));
        Discount discount = new Discount();
        discount.setDiscountPercentage(50);
        discount.setTotalDiscountedItems(1);
        discount.setTotalFullPriceItems(1);
        productDeal.setDiscount(discount);
        ProductDeal productDealSaved = productDealRepository.save(productDeal);

        int dealsBefore = productDealRepository.findAll().size();
        int discountsBefore = discountRepository.findAll().size();
        int bundlesBefore = bundleRepository.findAll().size();

        mockMvc.perform(delete(PRODUCT_DEALS_URI + "/" + productDealSaved.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // database assertions
        // deals
        int dealsAfter = productDealRepository.findAll().size();
        assertEquals(dealsBefore - 1, dealsAfter);
        assertEquals(0, dealsAfter);
        assertEquals(Optional.empty(), productDealRepository.findById(productDealSaved.getId()));

        // discounts
        int discountsAfter = discountRepository.findAll().size();
        assertEquals(discountsBefore - 1, discountsAfter);
        assertEquals(0, discountsAfter);
        assertEquals(Optional.empty(), discountRepository.findById(productDealSaved.getDiscount().getId()));

        // bundles
        int bundlesAfter = bundleRepository.findAll().size();
        assertEquals(bundlesBefore - 2, bundlesAfter);
        assertEquals(0, bundlesAfter);
    }

    @Test
    void deleteById_withNotExistingProductDeal_shouldThrowNotFound() throws Exception {
        mockMvc.perform(delete(PRODUCT_DEALS_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
