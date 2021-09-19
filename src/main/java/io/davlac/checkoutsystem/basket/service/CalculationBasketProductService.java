package io.davlac.checkoutsystem.basket.service;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.repository.BasketProductRepository;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductDetailsResponse;
import io.davlac.checkoutsystem.basket.service.dto.TotalBasketProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.davlac.checkoutsystem.basket.service.BundleCalculationBasketProductService.applyBundles;

@Service
@RequiredArgsConstructor
public class CalculationBasketProductService {

    private final BasketProductRepository basketProductRepository;
    private final DiscountCalculationBasketProductService discountCalculationBasketProductService;

    @Transactional(readOnly = true)
    public TotalBasketProductResponse calculateTotalPrice() {
        TotalBasketProductResponse response = new TotalBasketProductResponse();

        // basket product details
        List<BasketProduct> basketProducts = basketProductRepository.findAll();
        basketProducts.forEach(basketProduct -> response.getProductDetails()
                .add(discountCalculationBasketProductService.buildProductDetails(basketProduct)));

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
}
