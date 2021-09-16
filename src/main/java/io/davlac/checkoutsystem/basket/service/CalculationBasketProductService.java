package io.davlac.checkoutsystem.basket.service;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.repository.BasketProductRepository;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductDetailsResponse;
import io.davlac.checkoutsystem.basket.service.dto.TotalBasketProductResponse;
import io.davlac.checkoutsystem.basket.service.mapper.BasketProductMapper;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.productdeal.service.ProductDealService;
import io.davlac.checkoutsystem.productdeal.service.dto.response.DiscountResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.davlac.checkoutsystem.utils.NumbersUtils.roundUpBy2Decimals;

@Service
@RequiredArgsConstructor
public class CalculationBasketProductService {

    private static final int PERCENT_100 = 100;

    private final BasketProductRepository basketProductRepository;
    private final BasketProductMapper basketProductMapper;
    private final ProductDealService productDealService;

    @Transactional(readOnly = true)
    public TotalBasketProductResponse calculateTotalPrice() {
        TotalBasketProductResponse response = new TotalBasketProductResponse();

        // basket product details
        List<BasketProduct> basketProducts = basketProductRepository.findAll();
        basketProducts.forEach(basketProduct -> response.getProductDetails().add(buildDetails(basketProduct)));

        // total price
        response.setTotalPrice(calculateBasketTotalPrice(response.getProductDetails()));

        return response;
    }

    private static double calculateBasketTotalPrice(final List<BasketProductDetailsResponse> productDetailsList) {
        double[] totalPrice = {0};
        productDetailsList.forEach(productDetail -> totalPrice[0] += productDetail.getProductTotalPriceDiscounted());
        return roundUpBy2Decimals(totalPrice[0]);
    }

    private BasketProductDetailsResponse buildDetails(final BasketProduct basketProduct) {
        Product product = basketProduct.getProduct();
        List<ProductDealResponse> productDeals = productDealService.getAllByProductId(product.getId());
        BasketProductDetailsResponse productDetails = basketProductMapper.toDetailsResponse(basketProduct, productDeals);

        double totalProductPrice = calculateProductPrice(product.getPrice(), productDetails.getQuantity());
        productDetails.setProductTotalPrice(totalProductPrice);
        productDetails.setProductTotalPriceDiscounted(
                applyDiscountIfExist(product.getPrice(), totalProductPrice, basketProduct.getQuantity(), productDetails));

        return productDetails;
    }

    private static double calculateProductPrice(final double unitaryProductPrice, final int quantity) {
        double totalProductPrice = unitaryProductPrice * quantity;
        return roundUpBy2Decimals(totalProductPrice);
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

        double totalPriceOneDiscountGroup = calculatePriceForOneDiscountGroup(productPrice, discount);

        // apply discount total price group X time
        double discountedPrice = totalPriceOneDiscountGroup * nbDiscountToApply;

        // add products out of the discount
        discountedPrice += productPrice * nbProductsOutOfDiscount;

        return roundUpBy2Decimals(discountedPrice);
    }

    private static double calculatePriceForOneDiscountGroup(final double productPrice, DiscountResponse discount) {
        double totalFullPricedProducts = productPrice * discount.getTotalFullPriceItems();
        double totalDiscountedProducts = applyDiscountPercentage(productPrice, discount.getDiscountPercentage())
                * discount.getTotalDiscountedItems();
        return totalFullPricedProducts + totalDiscountedProducts;
    }

    private static double applyDiscountPercentage(final double value, final int percentage) {
        return value * (PERCENT_100 - percentage) / PERCENT_100;
    }
}
