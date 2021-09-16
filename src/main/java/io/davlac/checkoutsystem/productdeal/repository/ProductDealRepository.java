package io.davlac.checkoutsystem.productdeal.repository;

import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDealRepository extends JpaRepository<ProductDeal, Long> {

    List<ProductDeal> findAllByProduct(Product product);

}
