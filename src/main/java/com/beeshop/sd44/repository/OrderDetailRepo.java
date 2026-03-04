package com.beeshop.sd44.repository;

import com.beeshop.sd44.entity.Order;
import com.beeshop.sd44.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderDetailRepo extends JpaRepository<OrderDetail, UUID> {
    List<OrderDetail> getOrderDetailByOrder(Order order);
}
