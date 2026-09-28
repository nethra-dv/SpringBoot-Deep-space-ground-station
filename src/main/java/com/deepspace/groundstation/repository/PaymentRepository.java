package com.deepspace.groundstation.repository;

import com.deepspace.groundstation.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
