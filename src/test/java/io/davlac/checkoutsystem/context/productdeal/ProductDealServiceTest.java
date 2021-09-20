package io.davlac.checkoutsystem.context.productdeal;

import io.davlac.checkoutsystem.product.controller.error.BadRequestException;
import io.davlac.checkoutsystem.product.controller.error.NotFoundException;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.service.ProductService;
import io.davlac.checkoutsystem.productdeal.model.Bundle;
import io.davlac.checkoutsystem.productdeal.model.Discount;
import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import io.davlac.checkoutsystem.productdeal.repository.ProductDealRepository;
import io.davlac.checkoutsystem.productdeal.service.ProductDealService;
import io.davlac.checkoutsystem.productdeal.service.dto.request.BundleRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.DiscountRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.BundleResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.DiscountResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import io.davlac.checkoutsystem.productdeal.service.mapper.ProductDealMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDealServiceTest {

    private static final long PRODUCT_ID = 123L;
    private static final long PRODUCT_ID_2 = 456L;
    private static final int PERCENT_50 = 50;
    private static final int TOTAL_DISCOUNTED_ITEMS = 1;
    private static final int TOTAL_FULL_PRICE_ITEMS = 2;
    private static final int PERCENT_75 = 75;
    private static final long DISCOUNT_ID = 789L;
    private static final long BUNDLE_ID = 159L;
    private static final Instant LAST_MODIFIED_DATE = Instant.now();
    private static final long PRODUCT_DEAL_ID = 753L;

    @Mock
    private ProductDealRepository productDealRepository;
    @Mock
    private ProductDealMapper productDealMapper;
    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductDealService productDealService;

    ProductDeal productDeal = new ProductDeal();
    Product product = new Product(PRODUCT_ID);
    Product product2 = new Product(PRODUCT_ID_2);
    ProductDealResponse productResponse = new ProductDealResponse();

    @BeforeEach
    public void setUp() {
        productDeal.setProduct(product);
        productDeal.setDiscount(new Discount(TOTAL_FULL_PRICE_ITEMS, TOTAL_DISCOUNTED_ITEMS, PERCENT_50));
        productDeal.setBundles(Set.of(new Bundle(product2, PERCENT_75, productDeal)));
        productDeal.setLastModifiedDate(LAST_MODIFIED_DATE);

        productResponse.setId(PRODUCT_DEAL_ID);
        productResponse.setProductId(PRODUCT_ID);
        productResponse.setDiscount(
                new DiscountResponse(DISCOUNT_ID, TOTAL_FULL_PRICE_ITEMS, TOTAL_DISCOUNTED_ITEMS,
                        PERCENT_50, LAST_MODIFIED_DATE)
        );
        productResponse.setBundles(Set.of(
                new BundleResponse(BUNDLE_ID, PRODUCT_ID_2, PERCENT_75, LAST_MODIFIED_DATE)
        ));
        productResponse.setLastModifiedDate(LAST_MODIFIED_DATE);
    }

    @Test
    void create_withGoodRequest_shouldSavedProduct() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .withDiscount(
                        DiscountRequest.builder()
                                .withDiscountPercentage(PERCENT_50)
                                .withTotalDiscountedItems(TOTAL_DISCOUNTED_ITEMS)
                                .withTotalFullPriceItems(TOTAL_FULL_PRICE_ITEMS)
                                .build()
                )
                .withBundles(
                        Set.of(
                                BundleRequest.builder()
                                        .withProductId(PRODUCT_ID_2)
                                        .withDiscountPercentage(PERCENT_75)
                                        .build()
                        )
                )
                .build();

        when(productDealMapper.toEntity(request)).thenReturn(productDeal);
        when(productDealRepository.save(productDeal)).thenReturn(productDeal);
        when(productDealMapper.toDto(productDeal)).thenReturn(productResponse);

        ProductDealResponse response = productDealService.create(request);

        assertEquals(PRODUCT_DEAL_ID, response.getId());
        assertEquals(request.getProductId(), response.getProductId());

        assertEquals(DISCOUNT_ID, response.getDiscount().getId());
        assertEquals(request.getDiscount().getTotalFullPriceItems(), response.getDiscount().getTotalFullPriceItems());
        assertEquals(request.getDiscount().getTotalDiscountedItems(), response.getDiscount().getTotalDiscountedItems());
        assertEquals(request.getDiscount().getDiscountPercentage(), response.getDiscount().getDiscountPercentage());
        assertEquals(LAST_MODIFIED_DATE, response.getDiscount().getLastModifiedDate());

        BundleRequest bundleRequest = request.getBundles().stream().findFirst().get();
        BundleResponse bundleResponse = response.getBundles().stream().findFirst().get();
        assertEquals(BUNDLE_ID, bundleResponse.getId());
        assertEquals(bundleRequest.getProductId(), bundleResponse.getProductId());
        assertEquals(bundleRequest.getDiscountPercentage(), bundleResponse.getDiscountPercentage());
        assertEquals(LAST_MODIFIED_DATE, bundleResponse.getLastModifiedDate());

        assertEquals(LAST_MODIFIED_DATE, response.getLastModifiedDate());
    }

    @Test
    void create_withNoDiscount_shouldThrowBadRequest() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .build();

        try {
            productDealService.create(request);
        } catch (BadRequestException ex) {
            assertEquals("Discount and bundles are empty or null", ex.getMessage());
        }
    }

    @Test
    void create_withSameProductAndBundleProduct_shouldThrowBadRequest() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .withBundles(
                        Set.of(
                                BundleRequest.builder()
                                        .withProductId(PRODUCT_ID)
                                        .withDiscountPercentage(PERCENT_75)
                                        .build()
                        )
                )
                .build();

        try {
            productDealService.create(request);
        } catch (BadRequestException ex) {
            assertEquals("A bundle cannot contain the deal product", ex.getMessage());
        }
    }

    @Test
    void create_withAnotherBundle_shouldThrowBadRequest() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .withBundles(
                        Set.of(
                                BundleRequest.builder()
                                        .withProductId(PRODUCT_ID_2)
                                        .withDiscountPercentage(PERCENT_75)
                                        .build()
                        )
                )
                .build();

        when(productService.getEntityById(PRODUCT_ID)).thenReturn(product);
        when(productDealRepository.findAllByProduct(product)).thenReturn(List.of(productDeal));
        when(productDealMapper.toDto(productDeal)).thenReturn(productResponse);


        try {
            productDealService.create(request);
        } catch (BadRequestException ex) {
            assertEquals("Product deal can have only one bundle by product", ex.getMessage());
        }
    }

    @Test
    void create_withAnotherDiscount_shouldThrowBadRequest() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .withDiscount(
                        DiscountRequest.builder()
                                .withDiscountPercentage(PERCENT_50)
                                .withTotalDiscountedItems(TOTAL_DISCOUNTED_ITEMS)
                                .withTotalFullPriceItems(TOTAL_FULL_PRICE_ITEMS)
                                .build()
                )
                .build();

        when(productService.getEntityById(PRODUCT_ID)).thenReturn(product);
        when(productDealRepository.findAllByProduct(product)).thenReturn(List.of(productDeal));
        when(productDealMapper.toDto(productDeal)).thenReturn(productResponse);


        try {
            productDealService.create(request);
        } catch (BadRequestException ex) {
            assertEquals("Product deal can have only one discount by product", ex.getMessage());
        }
    }

    @Test
    void deleteById_withExistingProductDeal_shouldDeleteProduct() {
        when(productDealRepository.findById(PRODUCT_DEAL_ID)).thenReturn(Optional.of(productDeal));
        doNothing().when(productDealRepository).delete(productDeal);

        productDealService.deleteById(PRODUCT_DEAL_ID);

        verify(productDealRepository, times(1)).delete(productDeal);
    }

    @Test
    void deleteById_withNotExistingProductDeal_shouldThrowNotFound() {
        when(productDealRepository.findById(PRODUCT_DEAL_ID)).thenReturn(Optional.empty());

        try {
            productDealService.deleteById(PRODUCT_DEAL_ID);
        } catch (NotFoundException ex) {
            assertEquals("Product deal not found", ex.getMessage());
        }

        verify(productDealRepository, times(0)).delete(productDeal);
    }

}
