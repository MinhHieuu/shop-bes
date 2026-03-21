package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.response.NotificationResponse;
import com.beeshop.sd44.entity.Notification;
import com.beeshop.sd44.repository.NotificationRepo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepo notificationRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepo notificationRepo,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepo = notificationRepo;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Tạo thông báo mới → lưu DB → push realtime đến tất cả client đang subscribe /topic/notifications.
     *
     * @param title   Tiêu đề thông báo
     * @param message Nội dung thông báo
     * @param orderId ID hóa đơn liên quan
     * @param type    Loại thông báo (VD: "NEW_ORDER", "VNPAY_SUCCESS")
     */
    public NotificationResponse createAndBroadcast(String title, String message, UUID orderId, String type) {
        // 1. Lưu vào DB
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setOrderId(orderId);
        notification.setType(type);
        notification.setStatus(0); // chưa xử lý
        notification.setCreatedAt(new Date());
        notification = notificationRepo.save(notification);

        // 2. Build response
        NotificationResponse response = buildResponse(notification);

        // 3. Push WebSocket đến topic /topic/notifications
        messagingTemplate.convertAndSend("/topic/notifications", response);

        return response;
    }

    /**
     * Lấy danh sách thông báo chưa xử lý (status = 0).
     */
    public List<NotificationResponse> getUnread() {
        return notificationRepo
                .findByStatusOrderByCreatedAtDesc(0)
                .stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    /**
     * Đánh dấu thông báo đã xử lý (status = 1).
     * Trả về null nếu không tìm thấy.
     */
    public NotificationResponse markAsRead(UUID id) {
        Notification notification = notificationRepo.findById(id).orElse(null);
        if (notification == null) {
            return null;
        }
        notification.setStatus(1);
        notification = notificationRepo.save(notification);
        return buildResponse(notification);
    }

    // ───── Helper ─────

    private NotificationResponse buildResponse(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.setId(n.getId());
        r.setTitle(n.getTitle());
        r.setMessage(n.getMessage());
        r.setType(n.getType());
        r.setStatus(n.getStatus());
        r.setOrderId(n.getOrderId());
        r.setCreatedAt(n.getCreatedAt());
        return r;
    }
}
