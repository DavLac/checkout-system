package io.davlac.checkoutsystem.productdeal.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BundleResponse {

    private Long id;
    private Long productId;
    private Integer discountPercentage;
    private Instant lastModifiedDate;
}
