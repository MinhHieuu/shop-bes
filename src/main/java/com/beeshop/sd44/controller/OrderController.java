package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.OrderRequest;
import com.beeshop.sd44.dto.response.OrderResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("order")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("pay")
    public ResponseEntity<ApiResponse<OrderResponse>> handleOrder(@RequestBody OrderRequest orderRequest, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        OrderResponse response = this.orderService.hanldePlaceOrder(orderRequest, userId);
        return ResponseEntity.ok().body(new ApiResponse<>("thanh toan thanh cong", response));
    }
}
