package com.example.macarena_backend.repository;

import com.example.macarena_backend.entity.SoldOutProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SoldOutProductRepository extends JpaRepository<SoldOutProduct, Long> {

    /** All records for a product (active + expired). */
    List<SoldOutProduct> findByProductId(Long productId);

    /** All active records: expiresAt is null (manual) OR still in the future. */
    @Query("""
           select s from SoldOutProduct s
           where s.expiresAt is null or s.expiresAt > :now
           order by s.soldOutAt desc
           """)
    List<SoldOutProduct> findAllActive(@Param("now") LocalDateTime now);

    /** Active records for one product. */
    @Query("""
           select s from SoldOutProduct s
           where s.productId = :productId
             and (s.expiresAt is null or s.expiresAt > :now)
           """)
    List<SoldOutProduct> findActiveByProductId(@Param("productId") Long productId,
                                               @Param("now") LocalDateTime now);

    /** Active record for product + size (case-insensitive). */
    @Query("""
           select s from SoldOutProduct s
           where s.productId = :productId
             and lower(s.sizeLabel) = lower(:sizeLabel)
             and (s.expiresAt is null or s.expiresAt > :now)
           """)
    List<SoldOutProduct> findActiveByProductIdAndSize(@Param("productId") Long productId,
                                                      @Param("sizeLabel") String sizeLabel,
                                                      @Param("now") LocalDateTime now);

    /** Cleanup: delete only records that have an expiry date and it has passed. */
    @Modifying
    @Query("delete from SoldOutProduct s where s.expiresAt is not null and s.expiresAt < :now")
    void deleteExpired(@Param("now") LocalDateTime now);
}