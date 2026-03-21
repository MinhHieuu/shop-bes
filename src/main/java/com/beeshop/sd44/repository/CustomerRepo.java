package com.beeshop.sd44.repository;

import com.beeshop.sd44.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByUserId(UUID userId);
    Optional<Customer> findBySdt(String sdt);
}
