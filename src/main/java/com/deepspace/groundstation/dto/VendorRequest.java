package com.deepspace.groundstation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VendorRequest(
        @NotBlank String vendorName,
        String contactPerson,
        @Email String email,
        String phone,
        String address,
        Boolean active) {
}
