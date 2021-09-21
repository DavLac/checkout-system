package io.davlac.checkoutsystem.productdeal.service.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.davlac.checkoutsystem.productdeal.controller.validator.ProductExists;
import lombok.Getter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@ToString
@JsonDeserialize(builder = CreateProductDealRequest.Builder.class)
public final class CreateProductDealRequest {

    @NotNull
    @ProductExists
    private final Long productId;

    private final @Valid DiscountRequest discount;

    private final Set<@Valid BundleRequest> bundles;

    public CreateProductDealRequest(CreateProductDealRequest.Builder builder) {
        this.productId = builder.productId;
        this.discount = builder.discount;
        this.bundles = builder.bundles;
    }

    public static CreateProductDealRequest.Builder builder() {
        return new CreateProductDealRequest.Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private Long productId;
        private DiscountRequest discount;
        private Set<BundleRequest> bundles;

        public Builder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder withDiscount(DiscountRequest discount) {
            this.discount = discount;
            return this;
        }

        public Builder withBundles(Set<BundleRequest> bundles) {
            this.bundles = bundles;
            return this;
        }

        public CreateProductDealRequest build() {
            return new CreateProductDealRequest(this);
        }
    }
}
