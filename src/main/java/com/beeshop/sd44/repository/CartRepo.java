package com.beeshop.sd44.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.beeshop.sd44.entity.Cart;

@Repository
public interface CartRepo extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUser_Id(UUID userId);

    Optional<Cart> findByCustomerIdAndStatus(UUID customerId, Integer status);
}
