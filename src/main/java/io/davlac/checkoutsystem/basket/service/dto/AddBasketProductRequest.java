package io.davlac.checkoutsystem.basket.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@JsonDeserialize(builder = AddBasketProductRequest.Builder.class)
public class AddBasketProductRequest {

    @NotNull
    private final Long productId;

    @NotNull
    @Positive
    private final Integer quantity;

    public AddBasketProductRequest(AddBasketProductRequest.Builder builder) {
        this.productId = builder.productId;
        this.quantity = builder.quantity;
    }

    public static AddBasketProductRequest.Builder builder() {
        return new AddBasketProductRequest.Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private Long productId;
        private Integer quantity;

        public Builder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder withQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public AddBasketProductRequest build() {
            return new AddBasketProductRequest(this);
        }
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "AddBasketProductRequest{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
