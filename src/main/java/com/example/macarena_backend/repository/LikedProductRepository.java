package com.example.macarena_backend.repository;

import com.example.macarena_backend.entity.LikedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikedProductRepository extends JpaRepository<LikedProduct, Long> {

    List<LikedProduct> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<LikedProduct> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    long countByUserId(Long userId);
}