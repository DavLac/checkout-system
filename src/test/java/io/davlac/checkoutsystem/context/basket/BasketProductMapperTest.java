package io.davlac.checkoutsystem.context.basket;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductDetailsResponse;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
import io.davlac.checkoutsystem.basket.service.mapper.BasketProductMapper;
import io.davlac.checkoutsystem.basket.service.mapper.BasketProductMapperImpl;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.productdeal.service.dto.response.BundleResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.DiscountResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class BasketProductMapperTest {

    private static final long PRODUCT_ID = 123L;
    private static final long PRODUCT_ID_2 = 456L;
    private static final Instant LAST_MODIFIED_DATE = Instant.now();
    private static final int QUANTITY_10 = 10;
    private static final double PRICE = 12.34;

    private final BasketProductMapper basketProductMapper = new BasketProductMapperImpl();

    Product product = new Product(PRODUCT_ID);
    Product product2 = new Product(PRODUCT_ID_2);
    BasketProduct basketProduct = new BasketProduct();
    BasketProductResponse basketProductResponse = new BasketProductResponse();
    ProductDealResponse productDeal = new ProductDealResponse();

    @BeforeEach
    public void setUp() {
        basketProduct.setProductId(PRODUCT_ID);
        basketProduct.setProduct(product);
        basketProduct.setQuantity(1);
        basketProduct.setLastModifiedDate(LAST_MODIFIED_DATE);

        basketProductResponse.setProductId(PRODUCT_ID);
        basketProductResponse.setQuantity(1);
        basketProductResponse.setLastModifiedDate(LAST_MODIFIED_DATE);

        productDeal.setProductId(PRODUCT_ID);
        productDeal.setDiscount(new DiscountResponse());
        productDeal.setBundles(Set.of(new BundleResponse()));
        productDeal.setLastModifiedDate(LAST_MODIFIED_DATE);

        product.setPrice(PRICE);
    }

    @Test
    void toDto_withGoodRequest_shouldMapToDto() {
        BasketProductResponse response = basketProductMapper.toDto(basketProduct);

        assertEquals(basketProduct.getProductId(), response.getProductId());
        assertEquals(basketProduct.getQuantity(), response.getQuantity());
        assertEquals(basketProduct.getLastModifiedDate(), response.getLastModifiedDate());
    }

    @Test
    void toDto_withNullRequest_shouldReturnNull() {
        BasketProductResponse response = basketProductMapper.toDto(null);
        assertNull(response);
    }

    @Test
    void updateEntity_withGoodRequest_shouldUpdateEntity() {
        basketProductMapper.updateEntity(product2, QUANTITY_10, basketProduct);

        assertEquals(product2, basketProduct.getProduct());
        assertEquals(QUANTITY_10, basketProduct.getQuantity());
    }

    @Test
    void updateEntity_withNullProduct_shouldNotUpdateEntity() {
        basketProductMapper.updateEntity(null, QUANTITY_10, basketProduct);

        assertEquals(product, basketProduct.getProduct());
        assertEquals(1, basketProduct.getQuantity());
    }

    @Test
    void toDetailsResponse_withGoodRequest_shouldMapToDetailsResponse() {
        BasketProductDetailsResponse response = basketProductMapper
                .toDetailsResponse(basketProduct, List.of(productDeal));

        assertEquals(product.getPrice(), response.getProductPrice());
        assertEquals(product.getId(), response.getProductId());
        assertEquals(basketProduct.getQuantity(), response.getQuantity());
        assertEquals(1, response.getProductDeals().size());
    }

    @Test
    void toDetailsResponse_withBasketProductNull_shouldReturnNull() {
        BasketProductDetailsResponse response = basketProductMapper
                .toDetailsResponse(null, null);

        assertNull(response);
    }

    @Test
    void toDetailsResponse_withBasketProductNull_shouldReturnNull2() {
        BasketProductDetailsResponse response = basketProductMapper
                .toDetailsResponse(basketProduct, List.of());

        assertEquals(product.getPrice(), response.getProductPrice());
        assertEquals(product.getId(), response.getProductId());
        assertEquals(basketProduct.getQuantity(), response.getQuantity());
        assertEquals(0, response.getProductDeals().size());
    }
}
