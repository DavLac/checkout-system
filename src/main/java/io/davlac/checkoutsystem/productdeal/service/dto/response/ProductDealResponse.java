package io.davlac.checkoutsystem.productdeal.service.dto.response;

import java.time.Instant;
import java.util.List;

public class ProductDealResponse {

    private long id;

    private Long productId;

    private DiscountResponse discount;

    private List<BundleResponse> bundles;

    private Instant lastModifiedDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public DiscountResponse getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountResponse discount) {
        this.discount = discount;
    }

    public List<BundleResponse> getBundles() {
        return bundles;
    }

    public void setBundles(List<BundleResponse> bundles) {
        this.bundles = bundles;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "ProductDealResponse{" +
                "id=" + id +
                ", productId=" + productId +
                ", discount=" + discount +
                ", bundles=" + bundles +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
