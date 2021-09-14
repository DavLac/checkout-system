package io.davlac.checkoutsystem.integration.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public final class DiscountRequestTest {
    private final Integer totalFullPriceItems;
    private final Integer totalDiscountedItems;
    private final Integer discountPercentage;
}
