package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.EmployeeOrderRequest;
import com.beeshop.sd44.dto.request.OrderFilterRequest;
import com.beeshop.sd44.dto.response.OrderResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
//        if (!"CASH".equals(request.getPaymentMethod())) {
//            return ResponseEntity.badRequest().body(new ApiResponse<>("chi ho tro thanh toan tien mat", null));
//        }
        UUID employeeId = UUID.fromString(authentication.getName());
        OrderResponse response = orderService.handleCounterOrder(request, employeeId);
        System.out.println("employee - ship: ");

        return ResponseEntity.status(201).body(new ApiResponse<>("tao don hang thanh cong", response));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer paymentStatus,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) Long fromDate,
            @RequestParam(required = false) Long toDate) {

        // Nếu có filter param, sử dụng filtering
        if (status != null || paymentStatus != null || type != null ||
            paymentMethod != null || fromDate != null || toDate != null) {
            OrderFilterRequest filter = new OrderFilterRequest();
            filter.setStatus(status);
            filter.setPaymentStatus(paymentStatus);
            filter.setType(type);
            filter.setPaymentMethod(paymentMethod);
            if (fromDate != null) {
                filter.setFromDate(new Date(fromDate));
            }
            if (toDate != null) {
                filter.setToDate(new Date(toDate));
            }
            List<OrderResponse> list = orderService.getOrdersByFilter(filter);
            return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", list));
        }

        // Nếu không có filter, lấy tất cả
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
                                                                   @RequestParam("status") Integer status,
                                                                   Authentication authentication) {
        if (status == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("thieu trang thai", null));
        }
        OrderResponse response = orderService.getOrderResponseById(id);
        if (response == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        UUID operatorId = UUID.fromString(authentication.getName());
        orderService.updateOrderStatus(id, status, operatorId);
        return ResponseEntity.ok(new ApiResponse<>("cap nhat thanh cong", orderService.getOrderResponseById(id)));
    }
}
