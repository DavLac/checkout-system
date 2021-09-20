package io.davlac.checkoutsystem.context.basket;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.repository.BasketProductRepository;
import io.davlac.checkoutsystem.basket.service.BasketProductService;
import io.davlac.checkoutsystem.basket.service.dto.AddBasketProductRequest;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
import io.davlac.checkoutsystem.basket.service.mapper.BasketProductMapper;
import io.davlac.checkoutsystem.product.controller.error.NotFoundException;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasketProductServiceTest {

    private static final long PRODUCT_ID = 123L;
    private static final Instant LAST_MODIFIED_DATE = Instant.now();
    private static final int QUANTITY_10 = 10;

    @Mock
    private ProductService productService;
    @Mock
    private BasketProductRepository basketProductRepository;
    @Mock
    private BasketProductMapper basketProductMapper;

    @InjectMocks
    private BasketProductService basketProductService;

    Product product = new Product(PRODUCT_ID);
    BasketProduct basketProduct = new BasketProduct();
    BasketProductResponse basketProductResponse = new BasketProductResponse();

    @BeforeEach
    public void setUp() {
        basketProduct.setProductId(PRODUCT_ID);
        basketProduct.setProduct(product);
        basketProduct.setQuantity(1);
        basketProduct.setLastModifiedDate(LAST_MODIFIED_DATE);

        basketProductResponse.setProductId(PRODUCT_ID);
        basketProductResponse.setQuantity(1);
        basketProductResponse.setLastModifiedDate(LAST_MODIFIED_DATE);
    }

    @Test
    void addProduct_withGoodRequest_shouldReturnSavedProduct() {
        AddBasketProductRequest request = AddBasketProductRequest.builder()
                .withProductId(PRODUCT_ID)
                .withQuantity(1)
                .build();

        when(productService.getEntityById(PRODUCT_ID)).thenReturn(product);
        when(basketProductRepository.findByProduct(product)).thenReturn(Optional.of(basketProduct));
        when(basketProductRepository.save(any(BasketProduct.class))).thenReturn(basketProduct);
        when(basketProductMapper.toDto(basketProduct)).thenReturn(basketProductResponse);

        BasketProductResponse response = basketProductService.addProduct(request);

        assertEquals(request.getProductId(), response.getProductId());
        assertEquals(request.getQuantity(), response.getQuantity());
        assertEquals(LAST_MODIFIED_DATE, response.getLastModifiedDate());
    }

    @Test
    void addProduct_withGoodRequestAndEmptyBasket_shouldReturnSavedProduct() {
        AddBasketProductRequest request = AddBasketProductRequest.builder()
                .withProductId(PRODUCT_ID)
                .withQuantity(1)
                .build();

        when(productService.getEntityById(PRODUCT_ID)).thenReturn(product);
        when(basketProductRepository.findByProduct(product)).thenReturn(Optional.empty());
        when(basketProductRepository.save(any(BasketProduct.class))).thenReturn(basketProduct);
        when(basketProductMapper.toDto(basketProduct)).thenReturn(basketProductResponse);

        BasketProductResponse response = basketProductService.addProduct(request);

        assertEquals(request.getProductId(), response.getProductId());
        assertEquals(request.getQuantity(), response.getQuantity());
        assertEquals(LAST_MODIFIED_DATE, response.getLastModifiedDate());
    }

    @Test
    void patchByProductId_withGoodRequestAndEmptyBasket_shouldReturnSavedProduct() {
        when(productService.getEntityById(PRODUCT_ID)).thenReturn(product);
        when(basketProductRepository.findByProduct(product)).thenReturn(Optional.of(basketProduct));
        when(basketProductRepository.save(any(BasketProduct.class))).thenReturn(basketProduct);
        when(basketProductMapper.toDto(basketProduct)).thenReturn(basketProductResponse);

        BasketProductResponse response = basketProductService.patchByProductId(PRODUCT_ID, QUANTITY_10);

        assertEquals(basketProductResponse.getProductId(), response.getProductId());
        assertEquals(basketProductResponse.getQuantity(), response.getQuantity());
        assertEquals(basketProductResponse.getLastModifiedDate(), response.getLastModifiedDate());
    }

    @Test
    void patchByProductId_withNotExistingBasket_shouldThrowNotFound() {
        when(productService.getEntityById(PRODUCT_ID)).thenReturn(product);
        when(basketProductRepository.findByProduct(product)).thenReturn(Optional.empty());

        try {
            basketProductService.patchByProductId(PRODUCT_ID, QUANTITY_10);
        } catch (NotFoundException ex) {
            assertEquals(String.format("No basket product found with product ID = '%d'", PRODUCT_ID),
                    ex.getMessage());
        }
    }

    @Test
    void deleteByProductId_withGoodRequest_shouldDeleteBasketProduct() {
        when(productService.getEntityById(PRODUCT_ID)).thenReturn(product);
        when(basketProductRepository.findByProduct(product)).thenReturn(Optional.of(basketProduct));
        doNothing().when(basketProductRepository).delete(basketProduct);

        basketProductService.deleteByProductId(PRODUCT_ID);

        verify(basketProductRepository, times(1)).delete(basketProduct);
    }

    @Test
    void deleteByProductId_withNotExistingBasket_shouldThrowNotFound() {
        when(productService.getEntityById(PRODUCT_ID)).thenReturn(product);
        when(basketProductRepository.findByProduct(product)).thenReturn(Optional.empty());

        try {
            basketProductService.deleteByProductId(PRODUCT_ID);
        } catch (NotFoundException ex) {
            assertEquals(String.format("No basket product found with product ID = '%d'", PRODUCT_ID),
                    ex.getMessage());
        }

        verify(basketProductRepository, times(0)).delete(basketProduct);
    }
}
