package com.example.macarena_backend.service;

import com.example.macarena_backend.entity.AdminUser;
import com.example.macarena_backend.entity.Customer;
import com.example.macarena_backend.entity.PasswordResetOtp;
import com.example.macarena_backend.repository.AdminUserRepository;
import com.example.macarena_backend.repository.CustomerRepository;
import com.example.macarena_backend.repository.PasswordResetOtpRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int EXPIRY_MINUTES = 5;

    private final CustomerRepository customerRepo;
    private final AdminUserRepository adminRepo;
    private final PasswordResetOtpRepository otpRepo;
    private final PasswordEncoder encoder;
    private final JavaMailSender mailSender;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(CustomerRepository customerRepo,
                                AdminUserRepository adminRepo,
                                PasswordResetOtpRepository otpRepo,
                                PasswordEncoder encoder,
                                JavaMailSender mailSender) {
        this.customerRepo = customerRepo;
        this.adminRepo = adminRepo;
        this.otpRepo = otpRepo;
        this.encoder = encoder;
        this.mailSender = mailSender;
    }

    
    public void sendOtp(String email) {
        String mail = email.trim().toLowerCase();

        boolean isCustomer = customerRepo.findByEmailIgnoreCase(mail).isPresent();
        boolean isAdmin = adminRepo.findByEmailIgnoreCase(mail).isPresent();

        if (!isCustomer && !isAdmin) {
            throw new IllegalArgumentException("This email is not registered");
        }

        String otp = String.format("%06d", random.nextInt(1_000_000));

        PasswordResetOtp row = otpRepo.findByEmail(mail).orElseGet(PasswordResetOtp::new);
        row.setEmail(mail);
        row.setOtpHash(encoder.encode(otp));
        row.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES));
        row.setAttempts(0);
        otpRepo.save(row);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(mail);
        msg.setSubject("Your password reset OTP");
        msg.setText("Your OTP is " + otp + ". It is valid for " + EXPIRY_MINUTES
                + " minutes. If you did not request this, ignore this mail.");
        mailSender.send(msg);
    }
    // ---------- Step 2: OTP verify ----------
    public void verifyOtp(String email, String otp) {
        String mail = email.trim().toLowerCase();

        PasswordResetOtp row = otpRepo.findByEmail(mail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP"));

        if (row.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepo.deleteByEmail(mail);
            throw new IllegalArgumentException("OTP expired. Please request a new one.");
        }
        if (row.getAttempts() >= MAX_ATTEMPTS) {
            otpRepo.deleteByEmail(mail);
            throw new IllegalArgumentException("Too many wrong attempts. Request a new OTP.");
        }
        if (!encoder.matches(otp, row.getOtpHash())) {
            row.setAttempts(row.getAttempts() + 1);
            otpRepo.save(row);
            throw new IllegalArgumentException("Invalid OTP");
        }
    }

    // ---------- Step 3: Password reset ----------
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void resetPassword(String email, String otp, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        verifyOtp(email, otp);

        String mail = email.trim().toLowerCase();
        String hash = encoder.encode(newPassword);
        boolean updated = false;

        var customer = customerRepo.findByEmailIgnoreCase(mail);
        if (customer.isPresent()) {
            Customer c = customer.get();
            c.setPassword(hash);
            customerRepo.save(c);
            updated = true;
        }

        var admin = adminRepo.findByEmailIgnoreCase(mail);
        if (admin.isPresent()) {
            AdminUser a = admin.get();
            a.setPassword(hash);
            adminRepo.save(a);
            updated = true;
        }

        if (!updated) throw new IllegalArgumentException("Invalid request");

        otpRepo.deleteByEmail(mail);
    }
}