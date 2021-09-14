package io.davlac.checkoutsystem.integration.repository;

import io.davlac.checkoutsystem.productdeal.model.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleRepository extends JpaRepository<Bundle, Long> {

}
