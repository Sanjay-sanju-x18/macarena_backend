package com.example.macarena_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreatePaymentRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;     // in rupees, e.g. 500.00

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}