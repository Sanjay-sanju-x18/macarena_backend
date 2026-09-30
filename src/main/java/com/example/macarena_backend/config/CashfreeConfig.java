package com.example.macarena_backend.config;

import com.cashfree.pg.Cashfree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CashfreeConfig {

    @Value("${cashfree.app-id}")
    private String appId;

    @Value("${cashfree.secret-key}")
    private String secretKey;

    @Value("${cashfree.environment:SANDBOX}")
    private String environment;

    /**
     * Provides a configured Cashfree SDK client as a Spring bean.
     * Cashfree only has a 6-arg constructor — we pass nulls for the
     * partner fields since this app doesn't use partner API mode.
     */
    @Bean
    public Cashfree cashfreeClient() {
        Cashfree.CFEnvironment env = "PRODUCTION".equalsIgnoreCase(environment)
                ? Cashfree.CFEnvironment.PRODUCTION
                : Cashfree.CFEnvironment.SANDBOX;

        return new Cashfree(
                env,       // CFEnvironment
                appId,     // XClientId
                secretKey, // XClientSecret
                null,      // XPartnerAPIKey
                null,      // XPartnerMerchantID
                null       // XClientSignature
        );
    }
}