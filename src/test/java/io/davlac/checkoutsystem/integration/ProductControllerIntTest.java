package io.davlac.checkoutsystem.integration;

import io.davlac.checkoutsystem.config.JsonUtils;
import io.davlac.checkoutsystem.product.controller.ProductController;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.repository.ProductRepository;
import io.davlac.checkoutsystem.product.service.dto.CreateProductRequest;
import io.davlac.checkoutsystem.product.service.dto.PatchProductRequest;
import io.davlac.checkoutsystem.product.service.dto.ProductResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.core.IsNull;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

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
class ProductControllerIntTest {

    private static final String PRODUCTS_URI = "/products";
    private static final String NAME = "product_name";
    private static final String DESCRIPTION = "description";
    private static final String DESCRIPTION_2 = "description-2";
    private static final double PRICE = 12.34;
    private static final double PRICE_2 = 45.67;
    private static final double PRICE_TOO_MANY_DECIMALS = 12.456;
    private static final double PRICE_TOO_MANY_INT = 12345678912.0;
    private static final double PRICE_ZERO = 0;
    private static final double PRICE_NEGATIVE = -10;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JsonUtils jsonUtils;

    Product product = new Product();

    @BeforeEach
    public void setUp() {
        productRepository.deleteAll();

        product.setName(NAME);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);
    }

    @Test
    void createProduct_withGoodData_shouldReturnSavedProduct() throws Exception {
        int productsBefore = productRepository.findAll().size();

        CreateProductRequest createProductRequest = CreateProductRequest.builder()
                .withName(NAME)
                .withDescription(DESCRIPTION)
                .withPrice(PRICE)
                .build();

        // make request
        ResultActions resultActions = mockMvc.perform(post(PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createProductRequest)))
                .andExpect(status().isCreated());

        ProductResponse response = (ProductResponse) jsonUtils
                .deserializeResult(resultActions, ProductResponse.class);

        assertTrue(response.getId() > 0);
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertEquals(PRICE, response.getPrice());
        assertNotNull(response.getLastModifiedDate());

        // database assertions
        int productsAfter = productRepository.findAll().size();
        assertEquals(productsBefore + 1, productsAfter);
        assertEquals(1, productsAfter);
        assertNotNull(productRepository.findById(response.getId()).get());
    }

    @Test
    void createProduct_withWithoutDescription_shouldReturnNullDescription() throws Exception {
        CreateProductRequest createProductRequest = CreateProductRequest.builder()
                .withName(NAME)
                .withPrice(PRICE)
                .build();

        mockMvc.perform(post(PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(IsNull.nullValue()));
    }

    public static Stream<Arguments> createProductBadRequestParameters() {
        return Stream.of(
                Arguments.of(
                        "No name",
                        CreateProductRequest.builder()
                                .build()
                ),
                Arguments.of(
                        "Too short name",
                        CreateProductRequest.builder()
                                .withName(RandomStringUtils.randomAlphabetic(2))
                                .withPrice(PRICE)
                                .build()
                ),
                Arguments.of(
                        "Too long name",
                        CreateProductRequest.builder()
                                .withName(RandomStringUtils.randomAlphabetic(31))
                                .withPrice(PRICE)
                                .build()
                ),
                Arguments.of(
                        "Too short description",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE)
                                .withDescription(RandomStringUtils.randomAlphabetic(2))
                                .build()
                ),
                Arguments.of(
                        "Too long description",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE)
                                .withDescription(RandomStringUtils.randomAlphabetic(101))
                                .build()
                ),
                Arguments.of(
                        "No price",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .build()
                ),

                Arguments.of(
                        "Price too many decimals",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE_TOO_MANY_DECIMALS)
                                .build()
                ),
                Arguments.of(
                        "Price negative",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE_NEGATIVE)
                                .build()
                ),
                Arguments.of(
                        "Price zero",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE_ZERO)
                                .build()
                ),
                Arguments.of(
                        "Price too big",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withPrice(PRICE_TOO_MANY_INT)
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createProductBadRequestParameters")
    void createProduct_withErrors_shouldThrowBadRequest(String test,
                                                        CreateProductRequest createProductRequest) throws Exception {
        mockMvc.perform(post(PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createProductRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById_withExistingProduct_shouldDeleteProduct() throws Exception {
        Product savedProduct = productRepository.save(product);
        int productsBefore = productRepository.findAll().size();

        mockMvc.perform(delete(PRODUCTS_URI + "/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // database assertions
        int productsAfter = productRepository.findAll().size();
        assertEquals(productsBefore - 1, productsAfter);
        assertEquals(0, productsAfter);
        assertEquals(Optional.empty(), productRepository.findById(savedProduct.getId()));
    }

    @Test
    void deleteById_withNotExistingProduct_shouldThrowNotFound() throws Exception {
        mockMvc.perform(delete(PRODUCTS_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_withExistingProduct_shouldGetProduct() throws Exception {
        Product savedProduct = productRepository.save(product);

        ResultActions resultActions = mockMvc.perform(
                get(PRODUCTS_URI + "/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ProductResponse response = (ProductResponse) jsonUtils
                .deserializeResult(resultActions, ProductResponse.class);

        assertEquals(savedProduct.getId(), response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertEquals(PRICE, response.getPrice());
        assertEquals(savedProduct.getLastModifiedDate().truncatedTo(ChronoUnit.MILLIS),
                response.getLastModifiedDate().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void getById_withNotExistingProduct_shouldThrowNotFound() throws Exception {
        mockMvc.perform(get(PRODUCTS_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchById_withGoodData_shouldReturnPatchedProduct() throws Exception {
        Product savedProduct = productRepository.save(product);

        PatchProductRequest request = PatchProductRequest.builder()
                .withDescription(DESCRIPTION_2)
                .withPrice(PRICE_2)
                .build();

        // make request
        ResultActions resultActions = mockMvc.perform(
                patch(PRODUCTS_URI + "/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        ProductResponse response = (ProductResponse) jsonUtils
                .deserializeResult(resultActions, ProductResponse.class);

        assertEquals(savedProduct.getId(), response.getId());
        assertEquals(savedProduct.getName(), response.getName());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getPrice(), response.getPrice());
        assertEquals(savedProduct.getLastModifiedDate().truncatedTo(ChronoUnit.MILLIS),
                response.getLastModifiedDate().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void patchById_withOnlyDescription_shouldNotChangePrice() throws Exception {
        Product savedProduct = productRepository.save(product);

        PatchProductRequest request = PatchProductRequest.builder()
                .withDescription(DESCRIPTION_2)
                .build();

        // make request
        ResultActions resultActions = mockMvc.perform(
                patch(PRODUCTS_URI + "/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        ProductResponse response = (ProductResponse) jsonUtils
                .deserializeResult(resultActions, ProductResponse.class);

        assertEquals(savedProduct.getId(), response.getId());
        assertEquals(savedProduct.getName(), response.getName());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(savedProduct.getPrice(), response.getPrice());
        assertEquals(savedProduct.getLastModifiedDate().truncatedTo(ChronoUnit.MILLIS),
                response.getLastModifiedDate().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void patchById_withOnlyPrice_shouldNotChangeDescription() throws Exception {
        Product savedProduct = productRepository.save(product);

        PatchProductRequest request = PatchProductRequest.builder()
                .withPrice(PRICE_2)
                .build();

        // make request
        ResultActions resultActions = mockMvc.perform(
                patch(PRODUCTS_URI + "/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        ProductResponse response = (ProductResponse) jsonUtils
                .deserializeResult(resultActions, ProductResponse.class);

        assertEquals(savedProduct.getId(), response.getId());
        assertEquals(savedProduct.getName(), response.getName());
        assertEquals(savedProduct.getDescription(), response.getDescription());
        assertEquals(request.getPrice(), response.getPrice());
        assertEquals(savedProduct.getLastModifiedDate().truncatedTo(ChronoUnit.MILLIS),
                response.getLastModifiedDate().truncatedTo(ChronoUnit.MILLIS));
    }

    public static Stream<Arguments> patchProductBadRequestParameters() {
        return Stream.of(
                Arguments.of(
                        "Empty",
                        PatchProductRequest.builder()
                                .build()
                ),
                Arguments.of(
                        "Too short description",
                        PatchProductRequest.builder()
                                .withDescription(RandomStringUtils.randomAlphabetic(2))
                                .build()
                ),
                Arguments.of(
                        "Too long description",
                        PatchProductRequest.builder()
                                .withDescription(RandomStringUtils.randomAlphabetic(101))
                                .build()
                ),
                Arguments.of(
                        "Price too many decimals",
                        PatchProductRequest.builder()
                                .withPrice(PRICE_TOO_MANY_DECIMALS)
                                .build()
                ),
                Arguments.of(
                        "Price negative",
                        PatchProductRequest.builder()
                                .withPrice(PRICE_NEGATIVE)
                                .build()
                ),
                Arguments.of(
                        "Price zero",
                        PatchProductRequest.builder()
                                .withPrice(PRICE_ZERO)
                                .build()
                ),
                Arguments.of(
                        "Price too big",
                        PatchProductRequest.builder()
                                .withPrice(PRICE_TOO_MANY_INT)
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("patchProductBadRequestParameters")
    void patchById_withErrors_shouldThrowBadRequest(String test,
                                                    PatchProductRequest patchProductRequest) throws Exception {
        mockMvc.perform(patch(PRODUCTS_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(patchProductRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchById_withNotExistingProduct_shouldThrowNotFound() throws Exception {
        PatchProductRequest patchProductRequest = PatchProductRequest.builder()
                .withPrice(PRICE)
                .build();
        mockMvc.perform(patch(PRODUCTS_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(patchProductRequest)))
                .andExpect(status().isNotFound());
    }
}
