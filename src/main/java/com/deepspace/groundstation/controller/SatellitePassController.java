package com.deepspace.groundstation.controller;

import com.deepspace.groundstation.dto.SatellitePassRequest;
import com.deepspace.groundstation.dto.SatellitePassResponse;
import com.deepspace.groundstation.service.SatellitePassService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/space/passes")
public class SatellitePassController {
    private final SatellitePassService satellitePassService;

    public SatellitePassController(SatellitePassService satellitePassService) {
        this.satellitePassService = satellitePassService;
    }

    @PostMapping
    public ResponseEntity<SatellitePassResponse> create(@Valid @RequestBody SatellitePassRequest request) {
        SatellitePassResponse response = satellitePassService.create(request);
        return ResponseEntity.created(URI.create("/api/space/passes/" + response.id())).body(response);
    }

    @GetMapping
    public List<SatellitePassResponse> getAll() {
        return satellitePassService.getAll();
    }

    @GetMapping("/{id}")
    public SatellitePassResponse getById(@PathVariable Long id) {
        return satellitePassService.getById(id);
    }

    @PutMapping("/{id}")
    public SatellitePassResponse update(@PathVariable Long id, @Valid @RequestBody SatellitePassRequest request) {
        return satellitePassService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        satellitePassService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
