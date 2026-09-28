package com.deepspace.groundstation.repository;

import com.deepspace.groundstation.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
	boolean existsBySatellitePass_Id(Long satellitePassId);
	boolean existsByInvoiceNumber(String invoiceNumber);
}
