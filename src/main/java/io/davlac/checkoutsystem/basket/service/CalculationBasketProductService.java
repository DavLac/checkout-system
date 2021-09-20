package io.davlac.checkoutsystem.basket.service;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.repository.BasketProductRepository;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductDetailsResponse;
import io.davlac.checkoutsystem.basket.service.dto.TotalBasketProductResponse;
import io.davlac.checkoutsystem.basket.service.mapper.BasketProductMapper;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.productdeal.service.ProductDealService;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.davlac.checkoutsystem.basket.service.AbstractCalculationBasketProductService.calculateProductPrice;
import static io.davlac.checkoutsystem.basket.service.BundleCalculationBasketProductService.applyBundles;
import static io.davlac.checkoutsystem.basket.service.DiscountCalculationBasketProductService.applyDiscountIfExist;

@Service
@RequiredArgsConstructor
public class CalculationBasketProductService {

    private final BasketProductRepository basketProductRepository;
    private final BasketProductMapper basketProductMapper;
    private final ProductDealService productDealService;

    @Transactional(readOnly = true)
    public TotalBasketProductResponse calculateTotalPrice() {
        TotalBasketProductResponse response = new TotalBasketProductResponse();

        // basket product details
        List<BasketProduct> basketProducts = basketProductRepository.findAll();
        basketProducts.forEach(basketProduct -> response.getProductDetails().add(buildProductDetails(basketProduct)));

        response.getProductDetails().forEach(detail -> {
            double total = calculateProductPrice(detail.getProductPrice(), detail.getQuantity());
            // total without discounts
            detail.setProductTotalPriceBeforeDiscounts(total);
            // total after discount
            detail.setProductTotalPriceAfterDiscount(applyDiscountIfExist(detail, total));
        });
        // total with bundles
        applyBundles(response.getProductDetails());

        // total price
        response.setTotalPrice(calculateBasketTotalPrice(response.getProductDetails()));

        return response;
    }

    private static double calculateBasketTotalPrice(final List<BasketProductDetailsResponse> productDetailsList) {
        double[] totalPrice = {0};
        productDetailsList.forEach(productDetail ->
                // take the smallest total price between the bundled and discounted price
                totalPrice[0] += Math.min(productDetail.getProductTotalPriceAfterDiscount(),
                        productDetail.getProductTotalPriceAfterBundle()));
        return totalPrice[0];
    }

    private BasketProductDetailsResponse buildProductDetails(final BasketProduct basketProduct) {
        Product product = basketProduct.getProduct();
        List<ProductDealResponse> productDeals = productDealService.getAllByProductId(product.getId());
        return basketProductMapper.toDetailsResponse(basketProduct, productDeals);
    }
}
