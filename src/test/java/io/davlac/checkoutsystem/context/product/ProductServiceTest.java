package io.davlac.checkoutsystem.context.product;

import io.davlac.checkoutsystem.product.controller.error.BadRequestException;
import io.davlac.checkoutsystem.product.controller.error.NotFoundException;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.repository.ProductRepository;
import io.davlac.checkoutsystem.product.service.ProductService;
import io.davlac.checkoutsystem.product.service.dto.CreateProductRequest;
import io.davlac.checkoutsystem.product.service.dto.UpdateProductRequest;
import io.davlac.checkoutsystem.product.service.dto.ProductResponse;
import io.davlac.checkoutsystem.product.service.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final Long ID = 123L;
    private static final String NAME = "product_name";
    private static final String DESCRIPTION = "description";
    private static final double PRICE = 12.34;
    private static final String DESCRIPTION_2 = "description-2";
    private static final double PRICE_2 = 45.67;
    private static final Instant LAST_MODIFIED_DATE = Instant.now();

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    Product product = new Product();
    ProductResponse productResponse = new ProductResponse();

    @BeforeEach
    public void setUp() {
        product.setName(NAME);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);

        productResponse.setId(ID);
        productResponse.setPrice(PRICE);
        productResponse.setDescription(DESCRIPTION);
        productResponse.setName(NAME);
        productResponse.setLastModifiedDate(LAST_MODIFIED_DATE);
    }

    @Test
    void create_withGoodRequest_shouldSavedProduct() {
        CreateProductRequest request = CreateProductRequest.builder()
                .withName(NAME)
                .withDescription(DESCRIPTION)
                .withPrice(PRICE)
                .build();

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productResponse);

        ProductResponse response = productService.create(request);

        assertEquals(ID, response.getId());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getPrice(), response.getPrice());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(LAST_MODIFIED_DATE, response.getLastModifiedDate());
    }

    @Test
    void getById_withExistingProduct_shouldReturnProduct() {
        when(productRepository.findById(ID)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productResponse);

        ProductResponse response = productService.getById(ID);

        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(PRICE, response.getPrice());
        assertEquals(DESCRIPTION, response.getDescription());
        assertEquals(LAST_MODIFIED_DATE, response.getLastModifiedDate());
    }

    @Test
    void getById_withNotExistingProduct_shouldThrowNotFound() {
        when(productRepository.findById(ID)).thenReturn(Optional.empty());

        try {
            productService.getById(ID);
        } catch (NotFoundException ex) {
            assertEquals("Product not found", ex.getMessage());
        }
    }

    @Test
    void deleteById_withExistingProduct_shouldDeleteProduct() {
        when(productRepository.findById(ID)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        productService.deleteById(ID);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteById_withNotExistingProduct_shouldThrowNotFound() {
        when(productRepository.findById(ID)).thenReturn(Optional.empty());

        try {
            productService.deleteById(ID);
        } catch (NotFoundException ex) {
            assertEquals("Product not found", ex.getMessage());
        }

        verify(productRepository, times(0)).delete(product);
    }

    @Test
    void patchById_withGoodRequest_shouldSavedProduct() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .withDescription(DESCRIPTION_2)
                .withPrice(PRICE_2)
                .build();

        when(productRepository.findById(ID)).thenReturn(Optional.of(product));
        doNothing().when(productMapper).updateEntity(request, product);
        when(productRepository.save(product)).thenReturn(product);
        productResponse.setDescription(DESCRIPTION_2);
        productResponse.setPrice(PRICE_2);
        when(productMapper.toDto(product)).thenReturn(productResponse);

        ProductResponse response = productService.patchById(ID, request);

        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(PRICE_2, response.getPrice());
        assertEquals(DESCRIPTION_2, response.getDescription());
        assertEquals(LAST_MODIFIED_DATE, response.getLastModifiedDate());
    }

    @Test
    void patchById_withNotExistingProduct_shouldThrowNotFound() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .withDescription(DESCRIPTION_2)
                .withPrice(PRICE_2)
                .build();

        when(productRepository.findById(ID)).thenReturn(Optional.empty());

        try {
            productService.patchById(ID, request);
        } catch (NotFoundException ex) {
            assertEquals("Product not found", ex.getMessage());
        }
    }

    @Test
    void patchById_withEmptyRequest_shouldThrowBadRequest() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .build();

        try {
            productService.patchById(ID, request);
        } catch (BadRequestException ex) {
            assertEquals("Body is empty", ex.getMessage());
        }
    }
}
