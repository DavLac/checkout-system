package integration.product;

import io.davlac.checkoutsystem.product.controller.ProductController;
import io.davlac.checkoutsystem.product.service.dto.CreateProductRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.stream.Stream;

import static integration.config.JsonUtils.asJsonString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ProductController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class ProductControllerIntTest {

    private static final String PRODUCTS_URI = "/products";
    private static final String NAME = "product_name";
    private static final String DESCRIPTION = "description";
    private static final double PRICE = 1.0;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createProduct_withGoodData_shouldReturnSavedProduct() throws Exception {
        CreateProductRequest createProductRequest = CreateProductRequest.builder()
                .withName(NAME)
                .withDescription(DESCRIPTION)
                .withPrice(PRICE)
                .build();

        mockMvc.perform(post(PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is(createProductRequest.getName())))
                .andExpect(jsonPath("$.description", is(createProductRequest.getDescription())))
                .andExpect(jsonPath("$.price", is(createProductRequest.getPrice())));
    }

    @Test
    void createProduct_withWithoutDescription_shouldReturnNullDescription() throws Exception {
        CreateProductRequest createProductRequest = CreateProductRequest.builder()
                .withName(NAME)
                .build();

        mockMvc.perform(post(PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(IsNull.nullValue()));
    }

    @Test
    void createProduct_withWithoutPrice_shouldReturnPriceEqualZero() throws Exception {
        CreateProductRequest createProductRequest = CreateProductRequest.builder()
                .withName(NAME)
                .build();

        mockMvc.perform(post(PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price", is(0.0)));
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
                                .build()
                ),
                Arguments.of(
                        "Too long name",
                        CreateProductRequest.builder()
                                .withName(RandomStringUtils.randomAlphabetic(31))
                                .build()
                ),
                Arguments.of(
                        "Too short description",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withDescription(RandomStringUtils.randomAlphabetic(2))
                                .build()
                ),
                Arguments.of(
                        "Too long description",
                        CreateProductRequest.builder()
                                .withName(NAME)
                                .withDescription(RandomStringUtils.randomAlphabetic(101))
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
    void delete_withExistingProduct_shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete(PRODUCTS_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
