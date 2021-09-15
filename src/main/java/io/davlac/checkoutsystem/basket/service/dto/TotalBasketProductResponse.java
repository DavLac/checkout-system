package io.davlac.checkoutsystem.basket.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class TotalBasketProductResponse {

    private Double totalPrice;
    private Map<Long, BasketProductDetailsResponse> productDetails;

}
