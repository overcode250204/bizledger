package com.overcode250204.identityservice.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterTenantRequest(
        @NotBlank(message = "Tenant name is required")
        String tenantName,

        @NotBlank(message = "Tenant code is required")
        @Size(min = 3, max = 50, message = "Tenant code must be between 3 and 50 characters")
        String tenantCode,

        @NotBlank(message = "Owner email is required")
        @Email(message = "Owner email is invalid")
        String ownerEmail,

        @NotBlank(message = "Owner password is required")
        @Size(min = 8, message = "Owner password must have at least 8 characters")
        String ownerPassword,

        @NotBlank(message = "Owner full name is required")
        String ownerFullName
) {
}
