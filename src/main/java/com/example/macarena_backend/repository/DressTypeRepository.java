package com.example.macarena_backend.repository;

import com.example.macarena_backend.entity.DressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DressTypeRepository extends JpaRepository<DressType, Long> {

    Optional<DressType> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}