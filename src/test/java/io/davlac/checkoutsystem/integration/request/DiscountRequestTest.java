package io.davlac.checkoutsystem.integration.request;

public final class DiscountRequestTest {
    private final Integer totalFullPriceItems;
    private final Integer totalDiscountedItems;
    private final Integer discountPercentage;

    public DiscountRequestTest(DiscountRequestTest.Builder builder) {
        this.totalFullPriceItems = builder.totalFullPriceItems;
        this.totalDiscountedItems = builder.totalDiscountedItems;
        this.discountPercentage = builder.discountPercentage;
    }

    public static DiscountRequestTest.Builder builder() {
        return new DiscountRequestTest.Builder();
    }

    public static class Builder {
        private Integer totalFullPriceItems;
        private Integer totalDiscountedItems;
        private Integer discountPercentage;

        public Builder withTotalFullPriceItems(Integer totalFullPriceItems) {
            this.totalFullPriceItems = totalFullPriceItems;
            return this;
        }

        public Builder withTotalDiscountedItems(Integer totalDiscountedItems) {
            this.totalDiscountedItems = totalDiscountedItems;
            return this;
        }

        public Builder withDiscountPercentage(Integer discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }

        public DiscountRequestTest build() {
            return new DiscountRequestTest(this);
        }
    }

    public Integer getTotalFullPriceItems() {
        return totalFullPriceItems;
    }

    public Integer getTotalDiscountedItems() {
        return totalDiscountedItems;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }
}
