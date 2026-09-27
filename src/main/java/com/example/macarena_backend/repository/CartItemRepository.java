package com.example.macarena_backend.repository;

import com.example.macarena_backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCustomerIdOrderByIdDesc(Long customerId);

    Optional<CartItem> findByCustomerIdAndProductIdAndSize(
            Long customerId, Long productId, String size);

    void deleteByCustomerIdAndProductIdAndSize(
            Long customerId, Long productId, String size);

    void deleteByCustomerId(Long customerId);
}