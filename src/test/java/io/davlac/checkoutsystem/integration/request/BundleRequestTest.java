package io.davlac.checkoutsystem.integration.request;

public final class BundleRequestTest {

    private final Integer discountPercentage;

    public BundleRequestTest(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }
}
