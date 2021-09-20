package io.davlac.checkoutsystem.basket.service;

import io.davlac.checkoutsystem.basket.service.dto.BasketProductDetailsResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.DiscountResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;

import static io.davlac.checkoutsystem.basket.service.AbstractCalculationBasketProductService.calculateDealsTotalPrice;

public interface DiscountCalculationBasketProductService {

    static double applyDiscountIfExist(@NotNull final BasketProductDetailsResponse productDetails,
                                       final double totalProductPrice) {
        if (CollectionUtils.isEmpty(productDetails.getProductDeals())) {
            return totalProductPrice;
        }

        // a product should have 1 discount max
        Optional<DiscountResponse> productDiscount = productDetails.getProductDeals().stream()
                .map(ProductDealResponse::getDiscount)
                .filter(Objects::nonNull)
                .findFirst();

        if (productDiscount.isEmpty()) {
            return totalProductPrice;
        }

        DiscountResponse discount = productDiscount.get();

        if (discount.getDiscountQuantityTrigger() > productDetails.getQuantity()) {
            return totalProductPrice;
        }

        return calculateDiscount(productDetails.getProductPrice(), discount, productDetails.getQuantity());
    }

    private static double calculateDiscount(final double productPrice,
                                            final DiscountResponse discount,
                                            final int productQuantity) {
        int nbProductsOutOfDiscount = productQuantity % discount.getDiscountQuantityTrigger();
        int nbDiscountToApply = (productQuantity - nbProductsOutOfDiscount) / discount.getDiscountQuantityTrigger();

        double totalPriceOneDiscountGroup = calculateDealsTotalPrice(
                productPrice,
                discount.getTotalFullPriceItems(),
                discount.getTotalDiscountedItems(),
                discount.getDiscountPercentage()
        );

        // apply discount total price group X time
        double discountedPrice = totalPriceOneDiscountGroup * nbDiscountToApply;

        // add products out of the discount
        discountedPrice += productPrice * nbProductsOutOfDiscount;

        return discountedPrice;
    }
}
