package com.example.backend.dto;

/**
 * Generic API error response
 */
public record ErrorResponse(
        String message,
        String error,
        Integer status) {
}
