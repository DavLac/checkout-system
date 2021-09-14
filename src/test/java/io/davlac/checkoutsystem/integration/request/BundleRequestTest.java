package io.davlac.checkoutsystem.integration.request;

public final class BundleRequestTest {

    private final Long productId;
    private final Integer discountPercentage;

    public BundleRequestTest(BundleRequestTest.Builder builder) {
        this.productId = builder.productId;
        this.discountPercentage = builder.discountPercentage;
    }

    public static BundleRequestTest.Builder builder() {
        return new BundleRequestTest.Builder();
    }

    public static class Builder {
        private Long productId;
        private Integer discountPercentage;

        public Builder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder withDiscountPercentage(Integer discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }

        public BundleRequestTest build() {
            return new BundleRequestTest(this);
        }
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }
}
