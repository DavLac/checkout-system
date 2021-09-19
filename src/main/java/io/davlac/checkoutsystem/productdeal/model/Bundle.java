package io.davlac.checkoutsystem.productdeal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.davlac.checkoutsystem.product.model.Product;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "BUNDLES")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"productDeal"})
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer discountPercentage;

    @ManyToOne
    @JoinColumn(name = "productdeal_id", nullable = false)
    private ProductDeal productDeal;

    @LastModifiedDate
    private Instant lastModifiedDate;

    public Bundle(Product product, Integer discountPercentage, ProductDeal productDeal) {
        this.product = product;
        this.discountPercentage = discountPercentage;
        this.productDeal = productDeal;
    }
}
