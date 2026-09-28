package com.deepspace.groundstation.controller;

import com.deepspace.groundstation.dto.VendorRequest;
import com.deepspace.groundstation.dto.VendorResponse;
import com.deepspace.groundstation.service.VendorService;
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
@RequestMapping("/api/vendors")
public class VendorController {
    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PostMapping
    public ResponseEntity<VendorResponse> create(@Valid @RequestBody VendorRequest request) {
        VendorResponse response = vendorService.create(request);
        return ResponseEntity.created(URI.create("/api/vendors/" + response.id())).body(response);
    }

    @GetMapping
    public List<VendorResponse> getAll() {
        return vendorService.getAll();
    }

    @GetMapping("/{id}")
    public VendorResponse getById(@PathVariable Long id) {
        return vendorService.getById(id);
    }

    @PutMapping("/{id}")
    public VendorResponse update(@PathVariable Long id, @Valid @RequestBody VendorRequest request) {
        return vendorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vendorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
