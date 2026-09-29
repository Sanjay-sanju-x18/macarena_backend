package com.example.macarena_backend.repository;

import com.example.macarena_backend.entity.SoldOutProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SoldOutProductRepository extends JpaRepository<SoldOutProduct, Long> {

    /** All sold-out records still active for a product (not expired). */
    List<SoldOutProduct> findByProductIdAndExpiresAtAfter(Long productId, LocalDateTime now);

    /** All active sold-out records across all products. */
    List<SoldOutProduct> findByExpiresAtAfter(LocalDateTime now);

    /** By product + size (active). */
    List<SoldOutProduct> findByProductIdAndSizeLabelAndExpiresAtAfter(
            Long productId, String sizeLabel, LocalDateTime now);

    /** All records for a product (including expired). */
    List<SoldOutProduct> findByProductId(Long productId);

    /** Cleanup — delete expired records. */
    void deleteByExpiresAtBefore(LocalDateTime cutoff);
}