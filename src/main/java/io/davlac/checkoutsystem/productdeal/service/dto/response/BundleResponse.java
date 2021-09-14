package io.davlac.checkoutsystem.productdeal.service.dto.response;

import io.davlac.checkoutsystem.product.service.dto.ProductResponse;

import java.time.Instant;

public class BundleResponse {

    private long id;

    private ProductResponse product;

    private Integer discountPercentage;

    private Instant lastModifiedDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
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
                ", product=" + product +
                ", discountPercentage=" + discountPercentage +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
