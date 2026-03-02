package com.example.backend.exception;

import java.math.BigDecimal;

public class InsufficientBudgetException extends RuntimeException {

    public InsufficientBudgetException(String message) {
        super(message);
    }

    public InsufficientBudgetException(BigDecimal required, BigDecimal available) {
        super(String.format("Insufficient budget. Required: %.2f, Available: %.2f",
                required, available));
    }
}
