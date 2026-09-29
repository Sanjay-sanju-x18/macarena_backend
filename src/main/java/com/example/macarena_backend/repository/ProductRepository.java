package com.example.macarena_backend.repository;

import com.example.macarena_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Legacy — returns everything, including archived. */
    List<Product> findAllByOrderByIdDesc();

    /** Live products only (archived_at is null). Used by home page. */
    List<Product> findByArchivedAtIsNullOrderByIdDesc();

    /**
     * Finds fully sold-out products whose sold-out record is older than `cutoff`.
     * Used by ProductArchiveJob.
     */
    @Query("""
        SELECT p FROM Product p
        WHERE p.archivedAt IS NULL
          AND p.totalQty = 0
          AND EXISTS (
            SELECT 1 FROM SoldOutProduct s
            WHERE s.productId = p.id
              AND s.soldOutAt <= :cutoff
          )
    """)
    List<Product> findFullySoldOutCandidates(@Param("cutoff") LocalDateTime cutoff);
}