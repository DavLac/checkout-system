package io.davlac.checkoutsystem.context.product;

import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.service.dto.CreateProductRequest;
import io.davlac.checkoutsystem.product.service.dto.ProductResponse;
import io.davlac.checkoutsystem.product.service.dto.UpdateProductRequest;
import io.davlac.checkoutsystem.product.service.mapper.ProductMapper;
import io.davlac.checkoutsystem.product.service.mapper.ProductMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    private static final Long ID = 123L;
    private static final String NAME = "product_name";
    private static final String DESCRIPTION = "description";
    private static final double PRICE = 12.34;
    private static final String DESCRIPTION_2 = "description-2";
    private static final double PRICE_2 = 45.67;
    private static final Instant LAST_MODIFIED_DATE = Instant.now();

    private final ProductMapper productMapper = new ProductMapperImpl();

    @Test
    void toEntity_withGoodRequest_shouldMapToEntity() {
        CreateProductRequest request = CreateProductRequest.builder()
                .withName(NAME)
                .withDescription(DESCRIPTION)
                .withPrice(PRICE)
                .build();

        Product product = productMapper.toEntity(request);

        assertNull(product.getId());
        assertEquals(request.getName(), product.getName());
        assertEquals(request.getPrice(), product.getPrice());
        assertEquals(request.getDescription(), product.getDescription());
        assertNull(product.getLastModifiedDate());
    }

    @Test
    void toEntity_withNullRequest_shouldReturnNull() {
        Product product = productMapper.toEntity(null);
        assertNull(product);
    }

    @Test
    void toDto_withGoodProduct_shouldMapToDto() {
        Product product = new Product();
        product.setId(ID);
        product.setName(NAME);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);
        product.setLastModifiedDate(LAST_MODIFIED_DATE);

        ProductResponse response = productMapper.toDto(product);

        assertEquals(product.getId(), response.getId());
        assertEquals(product.getName(), response.getName());
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getDescription(), response.getDescription());
        assertEquals(product.getLastModifiedDate(), response.getLastModifiedDate());
    }

    @Test
    void toDto_withNullProduct_shouldReturnNull() {
        ProductResponse response = productMapper.toDto(null);
        assertNull(response);
    }

    @Test
    void updateEntity_withGoodRequest_shouldUpdateEntity() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .withDescription(DESCRIPTION_2)
                .withPrice(PRICE_2)
                .build();

        Product product = new Product();
        product.setId(ID);
        product.setName(NAME);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);
        product.setLastModifiedDate(LAST_MODIFIED_DATE);

        productMapper.updateEntity(request, product);

        assertEquals(ID, product.getId());
        assertEquals(NAME, product.getName());
        assertEquals(PRICE_2, product.getPrice());
        assertEquals(DESCRIPTION_2, product.getDescription());
        assertEquals(LAST_MODIFIED_DATE, product.getLastModifiedDate());
    }

    @Test
    void updateEntity_withEmptyRequest_shouldNotUpdateEntity() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .build();

        Product product = new Product();
        product.setId(ID);
        product.setName(NAME);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);
        product.setLastModifiedDate(LAST_MODIFIED_DATE);

        productMapper.updateEntity(request, product);

        assertEquals(ID, product.getId());
        assertEquals(NAME, product.getName());
        assertEquals(PRICE, product.getPrice());
        assertEquals(DESCRIPTION, product.getDescription());
        assertEquals(LAST_MODIFIED_DATE, product.getLastModifiedDate());
    }

    @Test
    void updateEntity_withJustPrice_shouldUpdateJustPrice() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .withPrice(PRICE_2)
                .build();

        Product product = new Product();
        product.setId(ID);
        product.setName(NAME);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);
        product.setLastModifiedDate(LAST_MODIFIED_DATE);

        productMapper.updateEntity(request, product);

        assertEquals(ID, product.getId());
        assertEquals(NAME, product.getName());
        assertEquals(PRICE_2, product.getPrice());
        assertEquals(DESCRIPTION, product.getDescription());
        assertEquals(LAST_MODIFIED_DATE, product.getLastModifiedDate());
    }

    @Test
    void updateEntity_withJustDescription_shouldUpdateJustDescription() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .withDescription(DESCRIPTION_2)
                .build();

        Product product = new Product();
        product.setId(ID);
        product.setName(NAME);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);
        product.setLastModifiedDate(LAST_MODIFIED_DATE);

        productMapper.updateEntity(request, product);

        assertEquals(ID, product.getId());
        assertEquals(NAME, product.getName());
        assertEquals(PRICE, product.getPrice());
        assertEquals(DESCRIPTION_2, product.getDescription());
        assertEquals(LAST_MODIFIED_DATE, product.getLastModifiedDate());
    }

    @Test
    void updateEntity_withNullRequest_shouldUpdateNothing() {
        Product product = new Product();
        product.setId(ID);
        product.setName(NAME);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);
        product.setLastModifiedDate(LAST_MODIFIED_DATE);

        productMapper.updateEntity(null, product);

        assertEquals(ID, product.getId());
        assertEquals(NAME, product.getName());
        assertEquals(PRICE, product.getPrice());
        assertEquals(DESCRIPTION, product.getDescription());
        assertEquals(LAST_MODIFIED_DATE, product.getLastModifiedDate());
    }
}
