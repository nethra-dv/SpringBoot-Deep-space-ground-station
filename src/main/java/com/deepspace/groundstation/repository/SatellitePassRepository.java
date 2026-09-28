package com.deepspace.groundstation.repository;

import com.deepspace.groundstation.entity.SatellitePass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SatellitePassRepository extends JpaRepository<SatellitePass, Long> {
}
