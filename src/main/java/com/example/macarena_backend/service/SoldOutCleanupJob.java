package com.example.macarena_backend.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SoldOutCleanupJob {

    private final SoldOutProductService service;

    public SoldOutCleanupJob(SoldOutProductService service) {
        this.service = service;
    }

    // Runs every hour — purges expired sold-out records
    @Scheduled(fixedRate = 3_600_000)
    public void cleanup() {
        service.purgeExpired();
    }
}