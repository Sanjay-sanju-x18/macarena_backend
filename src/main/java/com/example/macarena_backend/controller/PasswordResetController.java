package com.example.macarena_backend.controller;

import com.example.macarena_backend.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetService service;

    public PasswordResetController(PasswordResetService service) {
        this.service = service;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@RequestBody Map<String, String> body) {
        try {
            service.sendOtp(body.getOrDefault("email", ""));
            return ResponseEntity.ok(Map.of("message", "OTP sent to your email."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Could not send OTP. Try again."));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
        try {
            service.verifyOtp(body.getOrDefault("email", ""), body.getOrDefault("otp", ""));
            return ResponseEntity.ok(Map.of("message", "OTP verified"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@RequestBody Map<String, String> body) {
        try {
            service.resetPassword(
                    body.getOrDefault("email", ""),
                    body.getOrDefault("otp", ""),
                    body.get("newPassword"),
                    body.get("confirmPassword"));
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }
}