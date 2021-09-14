package io.davlac.checkoutsystem.productdeal.service.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class DiscountResponse {

    private Long id;
    private Integer totalFullPriceItems;
    private Integer totalDiscountedItems;
    private Integer discountPercentage;
    private Instant lastModifiedDate;
}
