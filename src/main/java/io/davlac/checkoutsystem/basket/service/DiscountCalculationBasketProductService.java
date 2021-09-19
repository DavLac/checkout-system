package io.davlac.checkoutsystem.basket.service;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductDetailsResponse;
import io.davlac.checkoutsystem.basket.service.mapper.BasketProductMapper;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.productdeal.service.ProductDealService;
import io.davlac.checkoutsystem.productdeal.service.dto.response.DiscountResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.davlac.checkoutsystem.basket.service.AbstractCalculationBasketProductService.calculateDealsTotalPrice;
import static io.davlac.checkoutsystem.basket.service.AbstractCalculationBasketProductService.calculateProductPrice;

@Service
@RequiredArgsConstructor
public class DiscountCalculationBasketProductService {

    private final BasketProductMapper basketProductMapper;
    private final ProductDealService productDealService;

    public BasketProductDetailsResponse buildProductDetails(final BasketProduct basketProduct) {
        Product product = basketProduct.getProduct();
        List<ProductDealResponse> productDeals = productDealService.getAllByProductId(product.getId());
        BasketProductDetailsResponse productDetails = basketProductMapper.toDetailsResponse(basketProduct, productDeals);

        double totalProductPrice = calculateProductPrice(product.getPrice(), productDetails.getQuantity());
        productDetails.setProductTotalPriceBeforeDiscounts(totalProductPrice);
        productDetails.setProductTotalPriceAfterDiscount(
                applyDiscountIfExist(product.getPrice(), totalProductPrice, basketProduct.getQuantity(), productDetails));

        return productDetails;
    }

    private static double applyDiscountIfExist(final double productPrice,
                                               final double totalProductPrice,
                                               final int quantity,
                                               final BasketProductDetailsResponse productDetails) {
        // a product should have 1 discount max
        Optional<DiscountResponse> productDiscount = productDetails.getProductDeals().stream()
                .map(ProductDealResponse::getDiscount)
                .filter(Objects::nonNull)
                .findFirst();

        if (productDiscount.isEmpty()) {
            return totalProductPrice;
        }

        DiscountResponse discount = productDiscount.get();

        if (discount.getDiscountQuantityTrigger() > quantity) {
            return totalProductPrice;
        }

        return calculateDiscount(productPrice, discount, quantity);
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
