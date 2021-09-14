package io.davlac.checkoutsystem.basket.model;

import io.davlac.checkoutsystem.product.model.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@Getter
@Setter
@ToString
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
}
