package integration.product;

import io.davlac.checkoutsystem.product.controller.ProductController;
import io.davlac.checkoutsystem.product.model.CreateProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

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

    private static final String PRODUCT_NAME = "product_name";
    private static final String PRODUCTS_URI = "/products";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createProduct_withGoodData_shouldReturnSavedProduct() throws Exception {
        CreateProductRequest createProductRequest = new CreateProductRequest();
        createProductRequest.setName(PRODUCT_NAME);

        mockMvc.perform(post(PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is(createProductRequest.getName())));
    }

    @Test
    void delete_withExistingProduct_shouldDeleteProduct() throws Exception {
        CreateProductRequest createProductRequest = new CreateProductRequest();
        createProductRequest.setName(PRODUCT_NAME);

        mockMvc.perform(delete(PRODUCTS_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createProductRequest)))
                .andExpect(status().isNoContent());
    }
}
