package com.example.macarena_backend.controller;

import com.example.macarena_backend.dto.AdminProfileRequest;
import com.example.macarena_backend.dto.AdminResponse;
import com.example.macarena_backend.dto.ChangePasswordRequest;
import com.example.macarena_backend.service.AdminProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin-profile")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminProfileController {

    private final AdminProfileService service;

    public AdminProfileController(AdminProfileService service) {
        this.service = service;
    }

    private Long currentAdminId(Authentication auth) {
        if (auth == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        Object details = auth.getDetails();
        if (details instanceof Long) return (Long) details;
        throw new IllegalArgumentException("Admin id missing from token");
    }

    @GetMapping
    public ResponseEntity<?> getProfile(Authentication auth) {
        try {
            Long adminId = currentAdminId(auth);
            return ResponseEntity.ok(service.getProfile(adminId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@Valid @RequestBody AdminProfileRequest req,
                                           Authentication auth) {
        try {
            Long adminId = currentAdminId(auth);
            return ResponseEntity.ok(service.updateProfile(adminId, req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req,
                                            Authentication auth) {
        try {
            Long adminId = currentAdminId(auth);
            service.changePassword(adminId, req);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}