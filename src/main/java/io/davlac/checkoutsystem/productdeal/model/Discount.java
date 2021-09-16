package io.davlac.checkoutsystem.productdeal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(0)
    private Integer totalFullPriceItems;

    @NotNull
    @Min(1)
    private Integer totalDiscountedItems;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer discountPercentage;

    @LastModifiedDate
    private Instant lastModifiedDate;

    public Discount(@NotNull @Min(0) Integer totalFullPriceItems,
                    @NotNull @Min(1) Integer totalDiscountedItems,
                    @NotNull @Min(0) @Max(100) Integer discountPercentage) {
        this.totalFullPriceItems = totalFullPriceItems;
        this.totalDiscountedItems = totalDiscountedItems;
        this.discountPercentage = discountPercentage;
    }
}
