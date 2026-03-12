package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.EmployeeOrderRequest;
import com.beeshop.sd44.dto.response.OrderResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employee/orders")
public class EmployeeOrderController {
    private final OrderService orderService;

    public EmployeeOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody EmployeeOrderRequest request,
                                                                  Authentication authentication) {
        if (!"CASH".equals(request.getPaymentMethod())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("chi ho tro thanh toan tien mat", null));
        }
        UUID employeeId = UUID.fromString(authentication.getName());
        OrderResponse response = orderService.handleCounterOrder(request, employeeId);
        return ResponseEntity.status(201).body(new ApiResponse<>("tao don hang thanh cong", response));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> list = orderService.getAllOrders();
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", list));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable("id") UUID id) {
        OrderResponse response = orderService.getOrderResponseById(id);
        if (response == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", response));
    }

    @PutMapping("{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable("id") UUID id,
                                                                   @RequestParam("status") Integer status) {
        if (status == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("thieu trang thai", null));
        }
        OrderResponse response = orderService.getOrderResponseById(id);
        if (response == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>("cap nhat thanh cong", orderService.getOrderResponseById(id)));
    }
}
