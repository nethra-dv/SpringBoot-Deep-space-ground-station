package com.deepspace.groundstation.repository;

import com.deepspace.groundstation.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByProductCode(String productCode);
}
