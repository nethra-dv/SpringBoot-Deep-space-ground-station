package com.deepspace.groundstation.service;

import com.deepspace.groundstation.dto.TelemetryRequest;
import com.deepspace.groundstation.dto.TelemetryResponse;
import com.deepspace.groundstation.entity.Dish;
import com.deepspace.groundstation.entity.SatellitePass;
import com.deepspace.groundstation.entity.Telemetry;
import com.deepspace.groundstation.exception.ResourceNotFoundException;
import com.deepspace.groundstation.repository.DishRepository;
import com.deepspace.groundstation.repository.SatellitePassRepository;
import com.deepspace.groundstation.repository.TelemetryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TelemetryService {
    private final TelemetryRepository telemetryRepository;
    private final SatellitePassRepository satellitePassRepository;
    private final DishRepository dishRepository;

    public TelemetryService(TelemetryRepository telemetryRepository,
                            SatellitePassRepository satellitePassRepository,
                            DishRepository dishRepository) {
        this.telemetryRepository = telemetryRepository;
        this.satellitePassRepository = satellitePassRepository;
        this.dishRepository = dishRepository;
    }

    public TelemetryResponse create(TelemetryRequest request) {
        Telemetry telemetry = new Telemetry();
        apply(request, telemetry);
        return toResponse(telemetryRepository.save(telemetry));
    }

    @Transactional(readOnly = true)
    public List<TelemetryResponse> getAll() {
        return telemetryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TelemetryResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public TelemetryResponse update(Long id, TelemetryRequest request) {
        Telemetry telemetry = findEntity(id);
        telemetry.setDataReceivedGB(request.dataReceivedGB());
        telemetry.setTrackingDurationHours(request.trackingDurationHours());
        telemetry.setElevation(request.elevation());
        telemetry.setAzimuth(request.azimuth());
        telemetry.setSignalStrength(request.signalStrength());
        telemetry.setMotorTemperature(request.motorTemperature());
        telemetry.setRecordedAt(request.recordedAt());
        return toResponse(telemetryRepository.save(telemetry));
    }

    public void delete(Long id) {
        telemetryRepository.delete(findEntity(id));
        }

        private Telemetry findEntity(Long id) {
        return telemetryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Telemetry with id " + id + " not found"));
        }

        private void apply(TelemetryRequest request, Telemetry telemetry) {
        SatellitePass satellitePass = satellitePassRepository.findById(request.satellitePassId()).orElseThrow(() ->
            new ResourceNotFoundException("Satellite pass with id " + request.satellitePassId() + " not found"));
        Dish dish = dishRepository.findById(request.dishId()).orElseThrow(() ->
            new ResourceNotFoundException("Dish with id " + request.dishId() + " not found"));
        telemetry.setSatellitePass(satellitePass);
        telemetry.setDish(dish);
        telemetry.setDataReceivedGB(request.dataReceivedGB());
        telemetry.setTrackingDurationHours(request.trackingDurationHours());
        telemetry.setElevation(request.elevation());
        telemetry.setAzimuth(request.azimuth());
        telemetry.setSignalStrength(request.signalStrength());
        telemetry.setMotorTemperature(request.motorTemperature());
        telemetry.setRecordedAt(request.recordedAt());
        }

        private TelemetryResponse toResponse(Telemetry telemetry) {
        return new TelemetryResponse(telemetry.getId(), telemetry.getSatellitePass().getId(),
            telemetry.getSatellitePass().getSatelliteName(), telemetry.getDish().getId(),
            telemetry.getDish().getDishCode(), telemetry.getDataReceivedGB(),
            telemetry.getTrackingDurationHours(), telemetry.getElevation(), telemetry.getAzimuth(),
            telemetry.getSignalStrength(), telemetry.getMotorTemperature(), telemetry.getRecordedAt());
    }
}
