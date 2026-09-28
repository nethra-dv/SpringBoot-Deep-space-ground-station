package com.deepspace.groundstation.controller;

import com.deepspace.groundstation.dto.TelemetryRequest;
import com.deepspace.groundstation.dto.TelemetryResponse;
import com.deepspace.groundstation.service.TelemetryService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/space/telemetry")
public class TelemetryController {
    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping
    public ResponseEntity<TelemetryResponse> create(@Valid @RequestBody TelemetryRequest request) {
        TelemetryResponse response = telemetryService.create(request);
        return ResponseEntity.created(URI.create("/api/space/telemetry/" + response.id())).body(response);
    }

    @GetMapping
    public List<TelemetryResponse> getAll() {
        return telemetryService.getAll();
    }

    @GetMapping("/{id}")
    public TelemetryResponse getById(@PathVariable Long id) {
        return telemetryService.getById(id);
    }

    @PutMapping("/{id}")
    public TelemetryResponse update(@PathVariable Long id, @Valid @RequestBody TelemetryRequest request) {
        return telemetryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        telemetryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
