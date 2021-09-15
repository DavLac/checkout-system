package io.davlac.checkoutsystem.basket.service.dto;

import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BasketProductDetailsResponse {

    private Integer quantity;
    private List<ProductDealResponse> basketProductDeals;

}
