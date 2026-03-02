package com.example.backend.dto;

/**
 * DTO for authentication response with JWT token
 */
public record AuthResponse(
        String token,
        String type,
        UserResponse user) {
    public AuthResponse(String token, UserResponse user) {
        this(token, "Bearer", user);
    }
}
