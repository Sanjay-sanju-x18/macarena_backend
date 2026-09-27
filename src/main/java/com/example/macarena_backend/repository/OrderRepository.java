package com.example.macarena_backend.repository;

import com.example.macarena_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByIdDesc(Long customerId);

    List<Order> findAllByOrderByIdDesc();
}