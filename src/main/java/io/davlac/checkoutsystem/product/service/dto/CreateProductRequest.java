package io.davlac.checkoutsystem.product.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@JsonDeserialize(builder = CreateProductRequest.Builder.class)
public final class CreateProductRequest {

    @NotEmpty
    @Size(min = 3, max = 30)
    private final String name;

    @Size(min = 3, max = 100)
    private final String description;

    private final double price;

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
        private double price;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withPrice(double price) {
            this.price = price;
            return this;
        }

        public CreateProductRequest build() {
            return new CreateProductRequest(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "CreateProductRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
