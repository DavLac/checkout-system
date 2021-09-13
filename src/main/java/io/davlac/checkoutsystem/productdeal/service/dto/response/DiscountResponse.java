package io.davlac.checkoutsystem.productdeal.service.dto.response;

import java.time.Instant;

public class DiscountResponse {

    private long id;

    private Integer totalFullPriceItems;

    private Integer totalDiscountedItems;

    private Integer discountPercentage;

    private Instant lastModifiedDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getTotalFullPriceItems() {
        return totalFullPriceItems;
    }

    public void setTotalFullPriceItems(Integer totalFullPriceItems) {
        this.totalFullPriceItems = totalFullPriceItems;
    }

    public Integer getTotalDiscountedItems() {
        return totalDiscountedItems;
    }

    public void setTotalDiscountedItems(Integer totalDiscountedItems) {
        this.totalDiscountedItems = totalDiscountedItems;
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
        return "DiscountResponse{" +
                "id=" + id +
                ", totalFullPriceItems=" + totalFullPriceItems +
                ", totalDiscountedItems=" + totalDiscountedItems +
                ", discountPercentage=" + discountPercentage +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
