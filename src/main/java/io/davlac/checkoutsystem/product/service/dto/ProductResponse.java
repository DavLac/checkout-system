package io.davlac.checkoutsystem.product.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Instant lastModifiedDate;
}
