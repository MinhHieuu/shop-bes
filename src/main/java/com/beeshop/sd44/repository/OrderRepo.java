package com.beeshop.sd44.repository;

import com.beeshop.sd44.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Quan trọng: Thêm dòng này
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepo extends JpaRepository<Order, UUID> {

    // Thống kê tổng doanh thu từ các đơn hàng có trạng thái là 1 (Đã thanh toán)
    @Query("SELECT SUM(o.total) FROM Order o WHERE o.status = 1")
    Double getTotalRevenue();

    // Đếm tổng số lượng đơn hàng có trong hệ thống
    @Query("SELECT COUNT(o) FROM Order o")
    Long countAllOrders();
}
