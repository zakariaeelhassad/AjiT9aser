package com.example.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO for user response (without sensitive information)
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        LocalDateTime createdAt) {
}
