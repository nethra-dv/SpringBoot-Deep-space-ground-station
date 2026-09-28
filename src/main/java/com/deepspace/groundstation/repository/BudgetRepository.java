package com.deepspace.groundstation.repository;

import com.deepspace.groundstation.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
