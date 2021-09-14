package io.davlac.checkoutsystem.productdeal.service.dto.response;

import io.davlac.checkoutsystem.product.service.dto.ProductResponse;

import java.time.Instant;
import java.util.Set;

public class ProductDealResponse {

    private Long id;

    private ProductResponse product;

    private DiscountResponse discount;

    private Set<BundleResponse> bundles;

    private Instant lastModifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }

    public DiscountResponse getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountResponse discount) {
        this.discount = discount;
    }

    public Set<BundleResponse> getBundles() {
        return bundles;
    }

    public void setBundles(Set<BundleResponse> bundles) {
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
                ", product=" + product +
                ", discount=" + discount +
                ", bundles=" + bundles +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
