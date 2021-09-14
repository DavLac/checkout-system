package io.davlac.checkoutsystem.productdeal.repository;

import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDealRepository extends JpaRepository<ProductDeal, Long> {

}
