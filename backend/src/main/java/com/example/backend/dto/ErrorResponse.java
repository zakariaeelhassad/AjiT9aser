package com.example.backend.dto;

public record ErrorResponse(
        String message,
        String error,
        Integer status) {
}
