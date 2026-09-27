package com.example.macarena_backend.controller;

import com.example.macarena_backend.dto.ChangePasswordRequest;
import com.example.macarena_backend.dto.CustomerProfileRequest;
import com.example.macarena_backend.dto.CustomerResponse;
import com.example.macarena_backend.service.CustomerProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerProfileController {

    private final CustomerProfileService service;

    public CustomerProfileController(CustomerProfileService service) {
        this.service = service;
    }

    private Long currentCustomerId(Authentication auth) {
        if (auth == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        Object details = auth.getDetails();
        if (details instanceof Long) return (Long) details;
        throw new IllegalArgumentException("Customer id missing from token");
    }

    // GET /api/profile
    @GetMapping
    public ResponseEntity<?> getProfile(Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);
            return ResponseEntity.ok(service.getProfile(customerId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/profile
    @PutMapping
    public ResponseEntity<?> updateProfile(@Valid @RequestBody CustomerProfileRequest req,
                                           Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);
            return ResponseEntity.ok(service.updateProfile(customerId, req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/profile/password
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req,
                                            Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);
            service.changePassword(customerId, req);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}