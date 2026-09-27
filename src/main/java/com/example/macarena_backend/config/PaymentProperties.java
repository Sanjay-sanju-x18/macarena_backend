package com.example.macarena_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentProperties {

    @Value("${payment.required:true}")
    private boolean paymentRequired;

    public boolean isPaymentRequired() {
        return paymentRequired;
    }
}