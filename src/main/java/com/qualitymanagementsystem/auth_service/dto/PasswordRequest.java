package com.qualitymanagementsystem.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PasswordRequest(
        @JsonProperty("email") String email,
        @JsonProperty("password") String password,
        @JsonProperty("repeatPassword") String repeatPassword) {
}