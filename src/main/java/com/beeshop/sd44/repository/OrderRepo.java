package com.beeshop.sd44.repository;

import com.beeshop.sd44.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepo extends JpaRepository<Order, UUID> {
	List<Order> findByUserId(UUID userId);
	List<Order> findAllByOrderByCreatedAtDesc();
}
