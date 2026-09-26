package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.LoginRequest;
import com.example.macarena_backend.dto.LoginResponse;
import com.example.macarena_backend.entity.AdminUser;
import com.example.macarena_backend.entity.Customer;
import com.example.macarena_backend.repository.AdminUserRepository;
import com.example.macarena_backend.repository.CustomerRepository;
import com.example.macarena_backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final CustomerRepository customerRepo;
    private final AdminUserRepository adminRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(CustomerRepository customerRepo,
                       AdminUserRepository adminRepo,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.customerRepo = customerRepo;
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest req) {

        String email = req.getEmail().trim().toLowerCase();
        String rawPassword = req.getPassword();

        // 1️⃣ Try CUSTOMER first (by email)
        Optional<Customer> customer = customerRepo.findByEmailIgnoreCase(email);
        if (customer.isPresent()) {
            Customer c = customer.get();

            if (!passwordEncoder.matches(rawPassword, c.getPassword())) {
                throw new IllegalArgumentException("Invalid email or password");
            }

            String token = jwtService.generateToken(c.getEmail(), "customer", c.getId());
            return new LoginResponse(
                    c.getId(),
                    c.getEmail(),
                    c.getFullName(),
                    "customer",
                    token,
                    "Login successful"
            );
        }

        // 2️⃣ Try ADMIN (matched by name — admins don't have email)
        Optional<AdminUser> admin = adminRepo.findByNameIgnoreCase(email);
        if (admin.isPresent()) {
            AdminUser a = admin.get();

            if (!passwordEncoder.matches(rawPassword, a.getPassword())) {
                throw new IllegalArgumentException("Invalid credentials");
            }

            String token = jwtService.generateToken(a.getName(), "admin", a.getId());
            return new LoginResponse(
                    a.getId(),
                    null,
                    a.getName(),
                    "admin",
                    token,
                    "Login successful"
            );
        }

        // 3️⃣ Neither found
        throw new IllegalArgumentException("Invalid credentials");
    }
}