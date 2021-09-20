package io.davlac.checkoutsystem.context.productdeal;

import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.productdeal.model.Bundle;
import io.davlac.checkoutsystem.productdeal.model.Discount;
import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import io.davlac.checkoutsystem.productdeal.service.dto.request.BundleRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.DiscountRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.BundleResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.DiscountResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import io.davlac.checkoutsystem.productdeal.service.mapper.ProductDealMapper;
import io.davlac.checkoutsystem.productdeal.service.mapper.ProductDealMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ProductDealMapperTest {

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

    private final ProductDealMapper productDealMapper = new ProductDealMapperImpl();

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
    void toEntity_withGoodRequest_shouldMapEntity() {
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

        ProductDeal response = productDealMapper.toEntity(request);

        assertNull(response.getId());
        assertEquals(request.getProductId(), response.getProduct().getId());

        assertNull(response.getDiscount().getId());
        assertEquals(request.getDiscount().getTotalFullPriceItems(), response.getDiscount().getTotalFullPriceItems());
        assertEquals(request.getDiscount().getTotalDiscountedItems(), response.getDiscount().getTotalDiscountedItems());
        assertEquals(request.getDiscount().getDiscountPercentage(), response.getDiscount().getDiscountPercentage());
        assertNull(response.getDiscount().getLastModifiedDate());

        BundleRequest bundleRequest = request.getBundles().stream().findFirst().get();
        Bundle bundle = response.getBundles().stream().findFirst().get();
        assertNull(bundle.getId());
        assertEquals(bundleRequest.getProductId(), bundle.getProduct().getId());
        assertEquals(bundleRequest.getDiscountPercentage(), bundle.getDiscountPercentage());
        assertNull(bundle.getLastModifiedDate());

        assertNull(response.getLastModifiedDate());
    }

    @Test
    void toEntity_withNullRequest_shouldReturnNull() {
        CreateProductDealRequest request = null;
        ProductDeal response = productDealMapper.toEntity(request);
        assertNull(response);
    }

    @Test
    void toEntityBundle_withNullRequest_shouldReturnNull() {
        BundleRequest request = null;
        Bundle response = productDealMapper.toEntity(request);
        assertNull(response);
    }

    @Test
    void toEntity_withNullDiscountRequest_shouldReturnNullDiscount() {
        CreateProductDealRequest request = CreateProductDealRequest.builder()
                .withProductId(PRODUCT_ID)
                .build();
        ProductDeal response = productDealMapper.toEntity(request);
        assertNull(response.getDiscount());
        assertNull(response.getBundles());
    }

    @Test
    void toDto_withGoodRequest_shouldMapDto() {
        ProductDealResponse response = productDealMapper.toDto(productDeal);

        assertEquals(productDeal.getId(), response.getId());
        assertEquals(productDeal.getProduct().getId(), response.getProductId());

        assertEquals(productDeal.getDiscount().getId(), response.getDiscount().getId());
        assertEquals(productDeal.getDiscount().getTotalFullPriceItems(), response.getDiscount().getTotalFullPriceItems());
        assertEquals(productDeal.getDiscount().getTotalDiscountedItems(), response.getDiscount().getTotalDiscountedItems());
        assertEquals(productDeal.getDiscount().getDiscountPercentage(), response.getDiscount().getDiscountPercentage());
        assertEquals(productDeal.getDiscount().getLastModifiedDate(), response.getDiscount().getLastModifiedDate());

        Bundle bundleRequest = productDeal.getBundles().stream().findFirst().get();
        BundleResponse bundle = response.getBundles().stream().findFirst().get();
        assertEquals(bundleRequest.getId(), bundle.getId());
        assertEquals(bundleRequest.getProduct().getId(), bundle.getProductId());
        assertEquals(bundleRequest.getDiscountPercentage(), bundle.getDiscountPercentage());
        assertEquals(bundleRequest.getLastModifiedDate(), bundle.getLastModifiedDate());

        assertEquals(productDeal.getLastModifiedDate(), response.getLastModifiedDate());
    }

    @Test
    void toDto_withNullDiscountAndBundleRequest_shouldReturnNull() {
        productDeal.setDiscount(null);
        productDeal.setBundles(null);
        ProductDealResponse response = productDealMapper.toDto(productDeal);
        assertNull(response.getDiscount());
        assertNull(response.getBundles());
    }

    @Test
    void toDto_withNullRequest_shouldReturnNull() {
        productDeal = null;
        ProductDealResponse response = productDealMapper.toDto(productDeal);
        assertNull(response);
    }

    @Test
    void toDto_withNullBundleRequest_shouldReturnNull() {
        Bundle request = null;
        BundleResponse response = productDealMapper.toDto(request);
        assertNull(response);
    }
}
