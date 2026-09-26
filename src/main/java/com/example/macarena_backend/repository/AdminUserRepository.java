package com.example.macarena_backend.repository;

import com.example.macarena_backend.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}