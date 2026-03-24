package com.example.backend.dto.common;

public record ErrorResponse(
        String message,
        String error,
        Integer status) {
}
