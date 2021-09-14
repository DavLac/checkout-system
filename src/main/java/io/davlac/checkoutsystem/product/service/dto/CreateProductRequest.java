package io.davlac.checkoutsystem.product.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@ToString
@JsonDeserialize(builder = CreateProductRequest.Builder.class)
public final class CreateProductRequest {

    @NotEmpty
    @Size(min = 3, max = 30)
    private final String name;

    @Size(min = 3, max = 100)
    private final String description;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    private final Double price;

    public CreateProductRequest(CreateProductRequest.Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
    }

    public static CreateProductRequest.Builder builder() {
        return new CreateProductRequest.Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private String name;
        private String description;
        private Double price;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withPrice(Double price) {
            this.price = price;
            return this;
        }

        public CreateProductRequest build() {
            return new CreateProductRequest(this);
        }
    }
}
