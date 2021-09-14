package io.davlac.checkoutsystem.productdeal.service.dto.response;

import io.davlac.checkoutsystem.product.service.dto.ProductResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class BundleResponse {

    private Long id;
    private ProductResponse product;
    private Integer discountPercentage;
    private Instant lastModifiedDate;
}
