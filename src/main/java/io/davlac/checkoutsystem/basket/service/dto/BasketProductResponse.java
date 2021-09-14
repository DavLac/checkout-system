package io.davlac.checkoutsystem.basket.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class BasketProductResponse {

    private Long productId;
    private Integer quantity;
    private Instant lastModifiedDate;
}
