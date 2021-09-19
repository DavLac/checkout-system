package io.davlac.checkoutsystem.basket.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static io.davlac.checkoutsystem.utils.NumbersUtils.roundUpBy2Decimals;

@Getter
@Setter
@ToString
public class TotalBasketProductResponse {

    private double totalPrice;
    private List<BasketProductDetailsResponse> productDetails = new ArrayList<>();

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = roundUpBy2Decimals(totalPrice);
    }
}
