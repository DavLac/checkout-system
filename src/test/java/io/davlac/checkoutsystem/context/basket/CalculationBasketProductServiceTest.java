package io.davlac.checkoutsystem.context.basket;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.repository.BasketProductRepository;
import io.davlac.checkoutsystem.basket.service.CalculationBasketProductService;
import io.davlac.checkoutsystem.basket.service.DiscountCalculationBasketProductService;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductDetailsResponse;
import io.davlac.checkoutsystem.basket.service.dto.TotalBasketProductResponse;
import io.davlac.checkoutsystem.basket.service.mapper.BasketProductMapper;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.productdeal.service.ProductDealService;
import io.davlac.checkoutsystem.productdeal.service.dto.response.BundleResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.DiscountResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculationBasketProductServiceTest {

    private static final long PRODUCT_ID = 123L;
    private static final long PRODUCT_ID_2 = 456L;
    private static final Instant LAST_MODIFIED_DATE = Instant.now();
    private static final int QUANTITY_10 = 10;
    public static final int QUANTITY = 1;
    public static final double PRICE = 10;
    private static final int PERCENT_50 = 50;
    private static final int TOTAL_DISCOUNTED_ITEMS = 1;
    private static final int TOTAL_FULL_PRICE_ITEMS = 2;
    private static final int PERCENT_75 = 75;
    private static final long DISCOUNT_ID = 789L;
    private static final long BUNDLE_ID = 159L;
    private static final long PRODUCT_DEAL_ID = 753L;

    @Mock
    private BasketProductRepository basketProductRepository;
    @Mock
    private BasketProductMapper basketProductMapper;
    @Mock
    private ProductDealService productDealService;

    @InjectMocks
    private CalculationBasketProductService calculationBasketProductService;

    Product product = new Product(PRODUCT_ID);
    Product product2 = new Product(PRODUCT_ID_2);
    BasketProduct basketProduct = new BasketProduct();
    BasketProduct basketProduct2 = new BasketProduct();
    BasketProductDetailsResponse detail = new BasketProductDetailsResponse();
    BasketProductDetailsResponse detail2 = new BasketProductDetailsResponse();
    ProductDealResponse productResponse = new ProductDealResponse();

    @BeforeEach
    public void setUp() {
        basketProduct.setProductId(PRODUCT_ID);
        basketProduct.setProduct(product);
        basketProduct.setQuantity(QUANTITY);
        basketProduct.setLastModifiedDate(LAST_MODIFIED_DATE);

        basketProduct2.setProductId(PRODUCT_ID_2);
        basketProduct2.setProduct(product2);
        basketProduct2.setQuantity(QUANTITY_10);
        basketProduct2.setLastModifiedDate(LAST_MODIFIED_DATE);

        detail.setProductId(PRODUCT_ID);
        detail.setQuantity(QUANTITY);
        detail.setProductDeals(List.of(productResponse));
        detail.setProductPrice(PRICE);
        detail.setProductTotalPriceBeforeDiscounts(PRICE);
        detail.setProductTotalPriceAfterDiscount(PRICE);

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

        detail2.setProductId(PRODUCT_ID_2);
        detail2.setQuantity(QUANTITY);
        detail2.setProductDeals(List.of());
        detail2.setProductPrice(PRICE);
        detail2.setProductTotalPriceBeforeDiscounts(PRICE);
        detail2.setProductTotalPriceAfterDiscount(PRICE);
    }

    @Test
    void calculateTotalPrice_with1BasketNoDeal_shouldReturnBasketDetailsAndTotalPrice() {
        when(basketProductRepository.findAll()).thenReturn(List.of(basketProduct));
        when(productDealService.getAllByProductId(product.getId())).thenReturn(List.of(productResponse));
        when(basketProductMapper.toDetailsResponse(basketProduct, List.of(productResponse)))
                .thenReturn(detail);

        TotalBasketProductResponse response = calculationBasketProductService.calculateTotalPrice();

        assertEquals(PRICE, response.getTotalPrice());
        assertEquals(1, response.getProductDetails().size());
        assertEquals(detail, response.getProductDetails().get(0));
    }

    @Test
    void calculateTotalPrice_with1Bundle_shouldApplyBundle() {
        when(basketProductRepository.findAll()).thenReturn(List.of(basketProduct, basketProduct2));

        when(productDealService.getAllByProductId(product.getId())).thenReturn(List.of(productResponse));
        when(basketProductMapper.toDetailsResponse(basketProduct, List.of(productResponse)))
                .thenReturn(detail);
        when(productDealService.getAllByProductId(product2.getId())).thenReturn(List.of());
        when(basketProductMapper.toDetailsResponse(basketProduct2, List.of()))
                .thenReturn(detail2);

        TotalBasketProductResponse response = calculationBasketProductService.calculateTotalPrice();

        assertEquals(PRICE + PRICE * 0.25, response.getTotalPrice());
        assertEquals(detail, response.getProductDetails().get(0));
        assertEquals(detail2, response.getProductDetails().get(1));
    }

    @Test
    void calculateTotalPrice_with1Bundle3Quantity_shouldApplyBundle3Times() {
        basketProduct.setQuantity(3);
        basketProduct2.setQuantity(3);
        productResponse.setBundles(Set.of(
                new BundleResponse(BUNDLE_ID, PRODUCT_ID_2, PERCENT_50, LAST_MODIFIED_DATE)
        ));
        productResponse.setDiscount(null);
        detail.setQuantity(3);
        detail2.setQuantity(3);
        when(basketProductRepository.findAll()).thenReturn(List.of(basketProduct, basketProduct2));

        when(productDealService.getAllByProductId(product.getId())).thenReturn(List.of(productResponse));
        when(basketProductMapper.toDetailsResponse(basketProduct, List.of(productResponse)))
                .thenReturn(detail);
        when(productDealService.getAllByProductId(product2.getId())).thenReturn(List.of());
        when(basketProductMapper.toDetailsResponse(basketProduct2, List.of()))
                .thenReturn(detail2);

        TotalBasketProductResponse response = calculationBasketProductService.calculateTotalPrice();

        assertEquals(3 * PRICE + 3 * PRICE * 0.5, response.getTotalPrice());
        assertEquals(detail, response.getProductDetails().get(0));
        assertEquals(detail2, response.getProductDetails().get(1));
    }

    @Test
    void calculateTotalPrice_with1Bundle1Quantity_shouldApplyBundle1Time() {
        basketProduct.setQuantity(1);
        basketProduct2.setQuantity(3);
        productResponse.setBundles(Set.of(
                new BundleResponse(BUNDLE_ID, PRODUCT_ID_2, PERCENT_75, LAST_MODIFIED_DATE)
        ));
        productResponse.setDiscount(null);
        detail.setQuantity(1);
        detail2.setQuantity(3);
        when(basketProductRepository.findAll()).thenReturn(List.of(basketProduct, basketProduct2));

        when(productDealService.getAllByProductId(product.getId())).thenReturn(List.of(productResponse));
        when(basketProductMapper.toDetailsResponse(basketProduct, List.of(productResponse)))
                .thenReturn(detail);
        when(productDealService.getAllByProductId(product2.getId())).thenReturn(List.of());
        when(basketProductMapper.toDetailsResponse(basketProduct2, List.of()))
                .thenReturn(detail2);

        TotalBasketProductResponse response = calculationBasketProductService.calculateTotalPrice();

        assertEquals(1 * PRICE + 1 * PRICE * 0.25 + 2 * PRICE, response.getTotalPrice());
        assertEquals(detail, response.getProductDetails().get(0));
        assertEquals(detail2, response.getProductDetails().get(1));
    }

    @Test
    void calculateTotalPrice_withNoBundle_shouldReturnFullPriceBundle() {
        when(basketProductRepository.findAll()).thenReturn(List.of(basketProduct));
        productResponse.setBundles(Set.of());
        detail.setProductDeals(List.of(productResponse));
        when(productDealService.getAllByProductId(product.getId())).thenReturn(List.of(productResponse));
        when(basketProductMapper.toDetailsResponse(basketProduct, List.of(productResponse)))
                .thenReturn(detail);

        TotalBasketProductResponse response = calculationBasketProductService.calculateTotalPrice();

        assertEquals(PRICE, response.getTotalPrice());
        assertEquals(detail, response.getProductDetails().get(0));
    }

    @Test
    void calculateTotalPrice_withDirectDiscount_shouldReturnDiscountPrice() {
        when(basketProductRepository.findAll()).thenReturn(List.of(basketProduct));
        productResponse.setBundles(Set.of());
        productResponse.setDiscount(
                new DiscountResponse(DISCOUNT_ID, 0, 1,
                        PERCENT_50, LAST_MODIFIED_DATE)
        );
        detail.setProductDeals(List.of(productResponse));
        when(productDealService.getAllByProductId(product.getId())).thenReturn(List.of(productResponse));
        when(basketProductMapper.toDetailsResponse(basketProduct, List.of(productResponse)))
                .thenReturn(detail);

        TotalBasketProductResponse response = calculationBasketProductService.calculateTotalPrice();

        assertEquals(PRICE / 2, response.getTotalPrice());
        assertEquals(detail, response.getProductDetails().get(0));
    }

    @Test
    void calculateTotalPrice_withGroupedDiscountNotEnoughProduct_shouldReturnNoDiscount() {
        when(basketProductRepository.findAll()).thenReturn(List.of(basketProduct));
        productResponse.setBundles(Set.of());
        productResponse.setDiscount(
                new DiscountResponse(DISCOUNT_ID, 5, 2,
                        PERCENT_50, LAST_MODIFIED_DATE)
        );
        detail.setProductDeals(List.of(productResponse));
        when(productDealService.getAllByProductId(product.getId())).thenReturn(List.of(productResponse));
        when(basketProductMapper.toDetailsResponse(basketProduct, List.of(productResponse)))
                .thenReturn(detail);

        TotalBasketProductResponse response = calculationBasketProductService.calculateTotalPrice();

        assertEquals(PRICE, response.getTotalPrice());
        assertEquals(detail, response.getProductDetails().get(0));
    }

    @Test
    void calculateTotalPrice_withGroupedDiscount_shouldDiscountedTotal() {
        when(basketProductRepository.findAll()).thenReturn(List.of(basketProduct));
        productResponse.setBundles(Set.of());
        productResponse.setDiscount(
                new DiscountResponse(DISCOUNT_ID, 5, 2,
                        PERCENT_50, LAST_MODIFIED_DATE)
        );
        basketProduct.setQuantity(7);
        detail.setQuantity(7);
        detail.setProductDeals(List.of(productResponse));
        when(productDealService.getAllByProductId(product.getId())).thenReturn(List.of(productResponse));
        when(basketProductMapper.toDetailsResponse(basketProduct, List.of(productResponse)))
                .thenReturn(detail);

        TotalBasketProductResponse response = calculationBasketProductService.calculateTotalPrice();

        assertEquals(PRICE * 5 + PRICE * 2 * 0.5, response.getTotalPrice());
        assertEquals(detail, response.getProductDetails().get(0));
    }

    @Test
    void calculateTotalPrice_withGroupedDiscountMultiple_shouldDiscountedTotalMultipleTimes() {
        when(basketProductRepository.findAll()).thenReturn(List.of(basketProduct));
        productResponse.setBundles(Set.of());
        productResponse.setDiscount(
                new DiscountResponse(DISCOUNT_ID, 2, 1,
                        PERCENT_50, LAST_MODIFIED_DATE)
        );
        basketProduct.setQuantity(10);
        detail.setQuantity(10);
        detail.setProductDeals(List.of(productResponse));
        when(productDealService.getAllByProductId(product.getId())).thenReturn(List.of(productResponse));
        when(basketProductMapper.toDetailsResponse(basketProduct, List.of(productResponse)))
                .thenReturn(detail);

        TotalBasketProductResponse response = calculationBasketProductService.calculateTotalPrice();

        assertEquals(PRICE * 2 * 3 + PRICE * 3 * 0.5 + PRICE, response.getTotalPrice());
        assertEquals(detail, response.getProductDetails().get(0));
    }

    @Test
    void calculateTotalPrice_withEmptyDiscount_shouldReturnFullPrice() {
        when(basketProductRepository.findAll()).thenReturn(List.of(basketProduct));
        productResponse.setBundles(Set.of());
        productResponse.setDiscount(null);
        detail.setProductDeals(List.of(productResponse));
        when(productDealService.getAllByProductId(product.getId())).thenReturn(List.of(productResponse));
        when(basketProductMapper.toDetailsResponse(basketProduct, List.of(productResponse)))
                .thenReturn(detail);

        TotalBasketProductResponse response = calculationBasketProductService.calculateTotalPrice();

        assertEquals(PRICE, response.getTotalPrice());
        assertEquals(detail, response.getProductDetails().get(0));
    }
}
