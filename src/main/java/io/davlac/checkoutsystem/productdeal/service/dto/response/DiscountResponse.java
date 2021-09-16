package io.davlac.checkoutsystem.productdeal.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@JsonIgnoreProperties({"discountQuantityTrigger"})
public class DiscountResponse {

    private Long id;
    private Integer totalFullPriceItems;
    private Integer totalDiscountedItems;
    private Integer discountPercentage;
    private Instant lastModifiedDate;

    public int getDiscountQuantityTrigger() {
        return this.totalFullPriceItems + this.totalDiscountedItems;
    }
}
