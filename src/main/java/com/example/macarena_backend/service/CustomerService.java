package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.CustomerResponse;
import com.example.macarena_backend.dto.RegisterRequest;
import com.example.macarena_backend.entity.Customer;
import com.example.macarena_backend.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository repository,
                           PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CustomerResponse register(RegisterRequest req) {

        // 1. Password match
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // 2. Normalize
        String fullName = req.getFullName().trim();
        String email = req.getEmail().trim().toLowerCase();
        String phone = req.getPhoneNumber().trim();

        // 3. Duplicate checks
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if (repository.existsByPhoneNumber(phone)) {
            throw new IllegalArgumentException("Phone number is already registered");
        }

        // 4. Save with BCrypt-hashed password
        Customer c = new Customer(
                fullName,
                email,
                phone,
                passwordEncoder.encode(req.getPassword())
        );

        Customer saved = repository.save(c);

        return new CustomerResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getPhoneNumber(),
                "Account created successfully"
        );
    }

    public List<CustomerResponse> getAll() {
        return repository.findAll().stream()
                .map(c -> new CustomerResponse(
                        c.getId(),
                        c.getFullName(),
                        c.getEmail(),
                        c.getPhoneNumber(),
                        null))
                .collect(Collectors.toList());
    }

    public CustomerResponse getById(Long id) {
        Customer c = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return new CustomerResponse(
                c.getId(),
                c.getFullName(),
                c.getEmail(),
                c.getPhoneNumber(),
                null
        );
    }

    @Transactional
    public void remove(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found");
        }
        repository.deleteById(id);
    }
}