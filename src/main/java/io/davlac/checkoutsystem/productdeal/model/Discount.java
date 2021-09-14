package io.davlac.checkoutsystem.productdeal.model;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "DISCOUNTS")
@EntityListeners(AuditingEntityListener.class)
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(0)
    private Integer totalFullPriceItems;

    @NotNull
    @Min(0)
    private Integer totalDiscountedItems;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer discountPercentage;

    @LastModifiedDate
    private Instant lastModifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return "Discount{" +
                "id=" + id +
                ", totalFullPriceItems=" + totalFullPriceItems +
                ", totalDiscountedItems=" + totalDiscountedItems +
                ", discountPercentage=" + discountPercentage +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
