package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.AdminRequest;
import com.example.macarena_backend.dto.AdminResponse;
import com.example.macarena_backend.entity.AdminUser;
import com.example.macarena_backend.repository.AdminUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AdminUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminUserRepository repository,
                        PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AdminResponse> getAll() {
        return repository.findAll().stream()
                .map(a -> new AdminResponse(
                        a.getId(),
                        a.getName(),
                        a.getCreatedAt(),
                        null))
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminResponse create(AdminRequest req) {

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String name = req.getName().trim();

        if (repository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("\"" + name + "\" already exists");
        }

        AdminUser admin = new AdminUser(
                name,
                passwordEncoder.encode(req.getPassword())
        );

        AdminUser saved = repository.save(admin);

        return new AdminResponse(
                saved.getId(),
                saved.getName(),
                saved.getCreatedAt(),
                "Admin created successfully"
        );
    }

    @Transactional
    public void remove(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Admin not found");
        }
        repository.deleteById(id);
    }
}