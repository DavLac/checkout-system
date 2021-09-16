package io.davlac.checkoutsystem.basket.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class TotalBasketProductResponse {

    private double totalPrice;
    private List<BasketProductDetailsResponse> productDetails = new ArrayList<>();

}
