package com.deepspace.groundstation.repository;

import com.deepspace.groundstation.entity.AnalyticAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticAccountRepository extends JpaRepository<AnalyticAccount, Long> {
	boolean existsByName(String name);
}
