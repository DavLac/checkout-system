package io.davlac.checkoutsystem.basket.service.dto;

import java.time.Instant;

public class BasketProductResponse {

    private Long productId;

    private Integer quantity;

    private Instant lastModifiedDate;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "BasketProductResponse{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
