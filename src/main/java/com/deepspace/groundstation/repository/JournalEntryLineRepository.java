package com.deepspace.groundstation.repository;

import com.deepspace.groundstation.entity.JournalEntryLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryLineRepository extends JpaRepository<JournalEntryLine, Long> {
}
