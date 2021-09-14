package io.davlac.checkoutsystem.productdeal.service.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@JsonDeserialize(builder = BundleRequest.Builder.class)
public final class BundleRequest {

    @NotNull
    private final Long productId;

    @NotNull
    @Min(0)
    @Max(100)
    private final Integer discountPercentage;

    public BundleRequest(BundleRequest.Builder builder) {
        this.productId = builder.productId;
        this.discountPercentage = builder.discountPercentage;
    }

    public static BundleRequest.Builder builder() {
        return new BundleRequest.Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private Long productId;
        private Integer discountPercentage;

        public Builder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder withDiscountPercentage(Integer discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }

        public BundleRequest build() {
            return new BundleRequest(this);
        }
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    @Override
    public String toString() {
        return "BundleRequest{" +
                "productId=" + productId +
                ", discountPercentage=" + discountPercentage +
                '}';
    }
}
