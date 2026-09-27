package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.AdminProfileRequest;
import com.example.macarena_backend.dto.AdminResponse;
import com.example.macarena_backend.dto.ChangePasswordRequest;
import com.example.macarena_backend.entity.AdminUser;
import com.example.macarena_backend.repository.AdminUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminProfileService {

    private final AdminUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AdminProfileService(AdminUserRepository repository,
                               PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminResponse getProfile(Long adminId) {
        AdminUser a = repository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        return toDto(a);
    }

    @Transactional
    public AdminResponse updateProfile(Long adminId, AdminProfileRequest req) {
        AdminUser a = repository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        String name = req.getName().trim();
        String email = req.getEmail().trim().toLowerCase();

        if (!a.getName().equalsIgnoreCase(name)
                && repository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("\"" + name + "\" is already taken");
        }
        if (!a.getEmail().equalsIgnoreCase(email)
                && repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        a.setName(name);
        a.setEmail(email);

        AdminUser saved = repository.save(a);
        return toDto(saved);
    }

    @Transactional
    public void changePassword(Long adminId, ChangePasswordRequest req) {
        AdminUser a = repository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), a.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        if (req.getCurrentPassword().equals(req.getNewPassword())) {
            throw new IllegalArgumentException("New password must differ from current");
        }

        a.setPassword(passwordEncoder.encode(req.getNewPassword()));
        repository.save(a);
    }

    private AdminResponse toDto(AdminUser a) {
        return new AdminResponse(
                a.getId(),
                a.getName(),
                a.getEmail(),
                a.getCreatedAt(),
                null
        );
    }
}