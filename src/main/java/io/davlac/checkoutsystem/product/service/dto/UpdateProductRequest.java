package io.davlac.checkoutsystem.product.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@ToString
@JsonDeserialize(builder = UpdateProductRequest.Builder.class)
public final class UpdateProductRequest {

    @Size(min = 3, max = 100)
    private final String description;

    @Positive
    @Digits(integer = 10, fraction = 2)
    private final Double price;

    public UpdateProductRequest(UpdateProductRequest.Builder builder) {
        this.description = builder.description;
        this.price = builder.price;
    }

    public static UpdateProductRequest.Builder builder() {
        return new UpdateProductRequest.Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private String description;
        private Double price;

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withPrice(Double price) {
            this.price = price;
            return this;
        }

        public UpdateProductRequest build() {
            return new UpdateProductRequest(this);
        }
    }

    public boolean isEmpty() {
        return Stream.of(this.price, this.description)
                .allMatch(Objects::isNull);
    }
}
