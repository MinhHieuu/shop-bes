package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.ChangePasswordRequest;
import com.beeshop.sd44.dto.response.OrderResponse;
import com.beeshop.sd44.dto.response.UserResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.Order;
import com.beeshop.sd44.entity.User;
import com.beeshop.sd44.service.OrderService;
import com.beeshop.sd44.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {
    private final UserService userService;
    private final OrderService orderService;
    private final PasswordEncoder passwordEncoder;

    public UserProfileController(UserService userService, OrderService orderService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.orderService = orderService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", userService.buildRespone(user)));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(Authentication authentication,
                                                                   @RequestBody User request) {
        UUID userId = UUID.fromString(authentication.getName());
        User updated = userService.updateProfile(userId, request);
        if (updated == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("cap nhat thanh cong", userService.buildRespone(updated)));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(Authentication authentication,
                                                             @RequestBody ChangePasswordRequest request) {
        UUID userId = UUID.fromString(authentication.getName());
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body(new ApiResponse<>("mat khau hien tai khong dung", null));
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.updateUser(user);
        return ResponseEntity.ok(new ApiResponse<>("doi mat khau thanh cong", null));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderHistory(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<OrderResponse> list = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", list));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(Authentication authentication,
                                                                     @PathVariable("id") UUID orderId) {
        UUID userId = UUID.fromString(authentication.getName());
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        if (!order.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body(new ApiResponse<>("khong co quyen", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", orderService.getOrderResponseById(orderId)));
    }
}
