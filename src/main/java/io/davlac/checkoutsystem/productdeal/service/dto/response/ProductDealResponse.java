package io.davlac.checkoutsystem.productdeal.service.dto.response;

import io.davlac.checkoutsystem.product.service.dto.ProductResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@ToString
public class ProductDealResponse {

    private Long id;
    private ProductResponse product;
    private DiscountResponse discount;
    private Set<BundleResponse> bundles;
    private Instant lastModifiedDate;
}
