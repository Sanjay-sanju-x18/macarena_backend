package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.ChangePasswordRequest;
import com.example.macarena_backend.dto.CustomerProfileRequest;
import com.example.macarena_backend.dto.CustomerResponse;
import com.example.macarena_backend.entity.Customer;
import com.example.macarena_backend.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerProfileService {

    private final CustomerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CustomerProfileService(CustomerRepository repository,
                                  PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomerResponse getProfile(Long customerId) {
        Customer c = repository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return toDto(c);
    }

    @Transactional
    public CustomerResponse updateProfile(Long customerId, CustomerProfileRequest req) {
        Customer c = repository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        String email = req.getEmail().trim().toLowerCase();
        String phone = req.getPhoneNumber().trim();

        if (!c.getEmail().equalsIgnoreCase(email)
                && repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if (!c.getPhoneNumber().equals(phone)
                && repository.existsByPhoneNumber(phone)) {
            throw new IllegalArgumentException("Phone number is already registered");
        }

        c.setFullName(req.getFullName().trim());
        c.setEmail(email);
        c.setPhoneNumber(phone);

        Customer saved = repository.save(c);
        return toDto(saved);
    }

    @Transactional
    public void changePassword(Long customerId, ChangePasswordRequest req) {
        Customer c = repository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), c.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        if (req.getCurrentPassword().equals(req.getNewPassword())) {
            throw new IllegalArgumentException("New password must differ from current");
        }

        c.setPassword(passwordEncoder.encode(req.getNewPassword()));
        repository.save(c);
    }

    private CustomerResponse toDto(Customer c) {
        return new CustomerResponse(
                c.getId(),
                c.getFullName(),
                c.getEmail(),
                c.getPhoneNumber(),
                null
        );
    }
}