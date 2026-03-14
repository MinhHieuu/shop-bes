package com.beeshop.sd44.repository;

import com.beeshop.sd44.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, UUID> {

    /**
     * Lấy tất cả thông báo theo trạng thái, sắp xếp mới nhất trước.
     * status=0 → chưa xử lý
     * status=1 → đã xử lý
     */
    List<Notification> findByStatusOrderByCreatedAtDesc(Integer status);
}
