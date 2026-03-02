package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user login
 */
public record LoginRequest(
        @NotBlank(message = "Email or username is required") String emailOrUsername,

        @NotBlank(message = "Password is required") String password) {
}
