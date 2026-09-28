package com.deepspace.groundstation.repository;

import com.deepspace.groundstation.entity.Telemetry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {
	List<Telemetry> findAllBySatellitePass_Id(Long satellitePassId);
}
