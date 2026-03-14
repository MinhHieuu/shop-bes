package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.response.NotificationResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST API quản lý thông báo (dành cho admin/employee trên dashboard).
 *
 * GET  /api/notifications        → lấy danh sách thông báo chưa xử lý (status=0)
 * PUT  /api/notifications/{id}/read → đánh dấu đã xử lý (status=1), trả về thông báo đã cập nhật
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Lấy tất cả thông báo chưa xử lý (status = 0).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnread() {
        List<NotificationResponse> list = notificationService.getUnread();
        return ResponseEntity.ok(new ApiResponse<>("lay thong bao thanh cong", list));
    }

    /**
     * Đánh dấu thông báo đã xử lý.
     * Frontend gọi khi người dùng bấm vào thông báo → điều hướng đến trang chi tiết hóa đơn.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable UUID id) {
        NotificationResponse response = notificationService.markAsRead(id);
        if (response == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay thong bao", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("da danh dau da xu ly", response));
    }
}
