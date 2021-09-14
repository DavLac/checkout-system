package io.davlac.checkoutsystem.integration.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@Builder
@ToString
public final class CreateProductDealRequestTest {

    private final DiscountRequestTest discount;
    private final Set<BundleRequestTest> bundles;
}
