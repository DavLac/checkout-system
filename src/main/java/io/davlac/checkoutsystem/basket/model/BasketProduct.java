package io.davlac.checkoutsystem.basket.model;

import io.davlac.checkoutsystem.product.model.Product;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "PRODUCT_BASKETS")
@EntityListeners(AuditingEntityListener.class)
public class BasketProduct {

    @Id
    private Long productId;

    @OneToOne
    @JoinColumn(name = "productId")
    @MapsId
    private Product product;

    private Integer quantity;

    @LastModifiedDate
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "BasketProduct{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", product=" + product +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
