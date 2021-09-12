package io.davlac.checkoutsystem.product.repository;

import io.davlac.checkoutsystem.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
