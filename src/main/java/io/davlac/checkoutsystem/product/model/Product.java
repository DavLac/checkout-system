package io.davlac.checkoutsystem.product.model;

import lombok.Getter;
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
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "PRODUCTS")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Size(min = 3, max = 30)
    private String name;

    @Size(min = 3, max = 100)
    private String description;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    private Double price;

    @LastModifiedDate
    private Instant lastModifiedDate;

    public Product() {
    }

    public Product(Long id) {
        this.id = id;
    }
}
