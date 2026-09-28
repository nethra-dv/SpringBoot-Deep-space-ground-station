package com.deepspace.groundstation.dto;

public record VendorResponse(
        Long id,
        String vendorName,
        String contactPerson,
        String email,
        String phone,
        String address,
        boolean active) {
}
