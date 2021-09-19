package io.davlac.checkoutsystem.basket.service.dto;

import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static io.davlac.checkoutsystem.utils.NumbersUtils.roundUpBy2Decimals;

@Getter
@Setter
@ToString
public class BasketProductDetailsResponse {

    private long productId;
    private double productPrice;
    private int quantity;
    private double productTotalPriceBeforeDiscounts;
    private double productTotalPriceAfterDiscount;
    private double productTotalPriceAfterBundle;
    private List<ProductDealResponse> productDeals;

    public void setProductTotalPriceBeforeDiscounts(double productTotalPriceBeforeDiscounts) {
        this.productTotalPriceBeforeDiscounts = roundUpBy2Decimals(productTotalPriceBeforeDiscounts);
    }

    public void setProductTotalPriceAfterDiscount(double productTotalPriceAfterDiscount) {
        this.productTotalPriceAfterDiscount = roundUpBy2Decimals(productTotalPriceAfterDiscount);
    }

    public void setProductTotalPriceAfterBundle(double productTotalPriceAfterBundle) {
        this.productTotalPriceAfterBundle = roundUpBy2Decimals(productTotalPriceAfterBundle);
    }
}
