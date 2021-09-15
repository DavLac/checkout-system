package io.davlac.checkoutsystem.basket.repository;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketProductRepository extends JpaRepository<BasketProduct, Product> {

    Optional<BasketProduct> findByProduct(Product product);

}
