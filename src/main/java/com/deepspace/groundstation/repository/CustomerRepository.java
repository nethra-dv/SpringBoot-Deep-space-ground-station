package com.deepspace.groundstation.repository;

import com.deepspace.groundstation.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
