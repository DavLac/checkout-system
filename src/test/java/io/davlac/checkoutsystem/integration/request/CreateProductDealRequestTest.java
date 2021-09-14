package io.davlac.checkoutsystem.integration.request;

import java.util.Set;

public final class CreateProductDealRequestTest {

    private final DiscountRequestTest discount;
    private final Set<BundleRequestTest> bundles;

    public CreateProductDealRequestTest(CreateProductDealRequestTest.Builder builder) {
        this.discount = builder.discount;
        this.bundles = builder.bundles;
    }

    public static CreateProductDealRequestTest.Builder builder() {
        return new CreateProductDealRequestTest.Builder();
    }

    public static class Builder {
        private DiscountRequestTest discount;
        private Set<BundleRequestTest> bundles;

        public Builder withDiscount(DiscountRequestTest discount) {
            this.discount = discount;
            return this;
        }

        public Builder withBundles(Set<BundleRequestTest> bundles) {
            this.bundles = bundles;
            return this;
        }

        public CreateProductDealRequestTest build() {
            return new CreateProductDealRequestTest(this);
        }
    }

    public DiscountRequestTest getDiscount() {
        return discount;
    }

    public Set<BundleRequestTest> getBundles() {
        return bundles;
    }
}
