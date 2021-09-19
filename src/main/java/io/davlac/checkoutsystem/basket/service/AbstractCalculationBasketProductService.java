package io.davlac.checkoutsystem.basket.service;

public abstract class AbstractCalculationBasketProductService {

    private static final int PERCENT_100 = 100;

    protected static double applyDiscountPercentage(final double value, final int percentage) {
        return value * (PERCENT_100 - percentage) / PERCENT_100;
    }

    protected static double calculateProductPrice(final double unitaryProductPrice, final int quantity) {
        return unitaryProductPrice * quantity;
    }

    protected static double calculateDealsTotalPrice(final double productPrice,
                                                     final int nbProductsFullPrice,
                                                     final int nbProductsDiscounted,
                                                     final int discountPercentage) {
        double totalPriceNoDiscount = nbProductsFullPrice * productPrice;
        double totalPriceDiscounted = nbProductsDiscounted * applyDiscountPercentage(productPrice, discountPercentage);
        return totalPriceNoDiscount + totalPriceDiscounted;
    }

}
