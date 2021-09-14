package io.davlac.checkoutsystem.productdeal.service.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@JsonDeserialize(builder = DiscountRequest.Builder.class)
public final class DiscountRequest {

    @NotNull
    @Min(0)
    private final Integer totalFullPriceItems;

    @NotNull
    @Min(1)
    private final Integer totalDiscountedItems;

    @NotNull
    @Min(0)
    @Max(100)
    private final Integer discountPercentage;

    public DiscountRequest(DiscountRequest.Builder builder) {
        this.totalFullPriceItems = builder.totalFullPriceItems;
        this.totalDiscountedItems = builder.totalDiscountedItems;
        this.discountPercentage = builder.discountPercentage;
    }

    public static DiscountRequest.Builder builder() {
        return new DiscountRequest.Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private Integer totalFullPriceItems;
        private Integer totalDiscountedItems;
        private Integer discountPercentage;

        public Builder withTotalFullPriceItems(Integer totalFullPriceItems) {
            this.totalFullPriceItems = totalFullPriceItems;
            return this;
        }

        public Builder withTotalDiscountedItems(Integer totalDiscountedItems) {
            this.totalDiscountedItems = totalDiscountedItems;
            return this;
        }

        public Builder withDiscountPercentage(Integer discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }

        public DiscountRequest build() {
            return new DiscountRequest(this);
        }
    }

    public Integer getTotalFullPriceItems() {
        return totalFullPriceItems;
    }

    public Integer getTotalDiscountedItems() {
        return totalDiscountedItems;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    @Override
    public String toString() {
        return "DiscountRequest{" +
                "totalFullPriceItems=" + totalFullPriceItems +
                ", totalDiscountedItems=" + totalDiscountedItems +
                ", discountPercentage=" + discountPercentage +
                '}';
    }
}
