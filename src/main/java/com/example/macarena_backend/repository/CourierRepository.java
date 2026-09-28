package com.example.macarena_backend.repository;

import com.example.macarena_backend.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    boolean existsByCompanyNameIgnoreCase(String companyName);
    boolean existsByCompanyNameIgnoreCaseAndIdNot(String companyName, Long id);
}