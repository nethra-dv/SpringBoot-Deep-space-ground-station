package com.deepspace.groundstation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.deepspace.groundstation.dto.TelemetryRequest;
import com.deepspace.groundstation.dto.TelemetryResponse;
import com.deepspace.groundstation.entity.Dish;
import com.deepspace.groundstation.entity.SatellitePass;
import com.deepspace.groundstation.entity.Telemetry;
import com.deepspace.groundstation.repository.DishRepository;
import com.deepspace.groundstation.repository.SatellitePassRepository;
import com.deepspace.groundstation.repository.TelemetryRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelemetryServiceTest {
    @Mock private TelemetryRepository telemetryRepository;
    @Mock private SatellitePassRepository satellitePassRepository;
    @Mock private DishRepository dishRepository;
    @InjectMocks private TelemetryService telemetryService;

    @Test
    void updateChangesMeasurementsWithoutReassigningPassOrDish() {
        SatellitePass originalPass = new SatellitePass();
        originalPass.setId(4L);
        originalPass.setSatelliteName("ORBIT-10");
        Dish originalDish = new Dish();
        originalDish.setId(2L);
        originalDish.setDishCode("DS-02");
        Telemetry existing = new Telemetry();
        existing.setId(19L);
        existing.setSatellitePass(originalPass);
        existing.setDish(originalDish);
        existing.setDataReceivedGB(new BigDecimal("250"));
        existing.setTrackingDurationHours(new BigDecimal("25"));
        existing.setRecordedAt(LocalDateTime.of(2026, 10, 1, 10, 25));
        when(telemetryRepository.findById(19L)).thenReturn(Optional.of(existing));
        when(telemetryRepository.save(any(Telemetry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TelemetryRequest request = new TelemetryRequest(999L, 888L, new BigDecimal("250"),
                new BigDecimal("0.42"), new BigDecimal("45"), new BigDecimal("180"),
                new BigDecimal("92"), new BigDecimal("42"), LocalDateTime.of(2026, 10, 1, 10, 25));

        TelemetryResponse response = telemetryService.update(19L, request);

        assertEquals(19L, response.id());
        assertEquals(4L, response.satellitePassId());
        assertEquals("ORBIT-10", response.satelliteName());
        assertEquals(2L, response.dishId());
        assertEquals("DS-02", response.dishCode());
        assertEquals(new BigDecimal("0.42"), response.trackingDurationHours());
        verify(telemetryRepository).save(existing);
    }

    @Test
    void deleteRemovesTheRequestedTelemetryId() {
        Telemetry existing = new Telemetry();
        existing.setId(19L);
        when(telemetryRepository.findById(19L)).thenReturn(Optional.of(existing));

        telemetryService.delete(19L);

        verify(telemetryRepository).delete(existing);
    }
}
