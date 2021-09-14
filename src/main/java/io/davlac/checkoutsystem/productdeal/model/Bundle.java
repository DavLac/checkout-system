package io.davlac.checkoutsystem.productdeal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.davlac.checkoutsystem.product.model.Product;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public ProductDeal getProductDeal() {
        return productDeal;
    }

    public void setProductDeal(ProductDeal productDeal) {
        this.productDeal = productDeal;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "Bundle{" +
                "id=" + id +
                ", product=" + product +
                ", discountPercentage=" + discountPercentage +
                ", productDeal=" + productDeal +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
