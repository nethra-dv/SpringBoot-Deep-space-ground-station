package com.deepspace.groundstation.repository;

import com.deepspace.groundstation.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
