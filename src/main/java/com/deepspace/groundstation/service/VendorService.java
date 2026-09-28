package com.deepspace.groundstation.service;

import com.deepspace.groundstation.dto.VendorRequest;
import com.deepspace.groundstation.dto.VendorResponse;
import com.deepspace.groundstation.entity.Vendor;
import com.deepspace.groundstation.exception.ResourceNotFoundException;
import com.deepspace.groundstation.repository.VendorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VendorService {
    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public VendorResponse create(VendorRequest request) {
        Vendor vendor = new Vendor();
        apply(request, vendor);
        return toResponse(vendorRepository.save(vendor));
    }

    @Transactional(readOnly = true)
    public List<VendorResponse> getAll() {
        return vendorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public VendorResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public VendorResponse update(Long id, VendorRequest request) {
        Vendor vendor = findEntity(id);
        apply(request, vendor);
        return toResponse(vendorRepository.save(vendor));
    }

    public void delete(Long id) {
        vendorRepository.delete(findEntity(id));
    }

    private Vendor findEntity(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor with id " + id + " not found"));
    }

    private void apply(VendorRequest request, Vendor vendor) {
        vendor.setVendorName(request.vendorName());
        vendor.setContactPerson(request.contactPerson());
        vendor.setEmail(request.email());
        vendor.setPhone(request.phone());
        vendor.setAddress(request.address());
        vendor.setActive(request.active() == null || request.active());
    }

    private VendorResponse toResponse(Vendor vendor) {
        return new VendorResponse(vendor.getId(), vendor.getVendorName(), vendor.getContactPerson(),
                vendor.getEmail(), vendor.getPhone(), vendor.getAddress(), vendor.isActive());
    }
}
