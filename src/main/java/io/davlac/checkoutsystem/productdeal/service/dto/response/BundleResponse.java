package io.davlac.checkoutsystem.productdeal.service.dto.response;

import java.time.Instant;

public class BundleResponse {

    private long id;

    private Long productId;

    private Integer discountPercentage;

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

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "BundleResponse{" +
                "id=" + id +
                ", productId=" + productId +
                ", discountPercentage=" + discountPercentage +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
