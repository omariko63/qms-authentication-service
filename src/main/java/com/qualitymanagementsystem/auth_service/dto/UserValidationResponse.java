package com.qualitymanagementsystem.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserValidationResponse(Long id, String username, String email, RoleDto role) {
}
