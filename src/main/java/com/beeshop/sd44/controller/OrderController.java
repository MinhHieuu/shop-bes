package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.OrderRequest;
import com.beeshop.sd44.dto.response.OrderResponse;
import com.beeshop.sd44.dto.response.VNPayResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.Order;
import com.beeshop.sd44.service.OrderService;
import com.beeshop.sd44.service.VNPayService;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    private final VNPayService vnPayService;

    public OrderController(OrderService orderService, VNPayService vnPayService) {
        this.orderService = orderService;
        this.vnPayService = vnPayService;
    }

    @PostMapping("pay")
    public ResponseEntity<ApiResponse<Object>> handleOrder(@RequestBody OrderRequest orderRequset,
                                                           Authentication authentication, HttpServletRequest request) throws ServletException, IOException {
        UUID userId = UUID.fromString(authentication.getName());
        OrderResponse orderResponse = orderService.hanldePlaceOrder(orderRequset, userId);
        if (orderRequset.getPaymentMethod().equals("COD")) {
            return ResponseEntity.ok().body(new ApiResponse<>("tao don hang thanh cong", orderResponse));
        } else {
            VNPayResponse vnPayResponse = this.vnPayService.createPaymentLink(orderResponse.getId().toString(),
                    orderRequset.getTotal().longValue(),
                    "Thanh toan don hang : " + orderResponse.getCode(), request);
            if (vnPayResponse == null || !vnPayResponse.isSuccess()) {
                return ResponseEntity.internalServerError()
                        .body(new ApiResponse<>("tao link thanh toan that bai", null));
            }
            return ResponseEntity.ok().body(new ApiResponse<>("tao link thanh toan thanh cong", vnPayResponse));
        }
    }

    @GetMapping("vnpay-return")
    public void  handleVNPayReturn(@RequestParam Map<String, String> params, HttpServletResponse response)
            throws IOException {
        System.out.println("VNPay return received: " + params);
        try {
            // Verify signature
            if (!vnPayService.verifyPayment(params)) {
                ResponseEntity.badRequest().body(new ApiResponse<>("Chữ ký không hợp lệ", null));
            }

            String orderId = params.get("vnp_TxnRef");
            String responseCode = params.get("vnp_ResponseCode");

            if ("00".equals(responseCode)) {
                // Thanh toán thành công
                Order order = orderService.updateOrderStatus(UUID.fromString(orderId), 1);
                response.sendRedirect("localhost:8080");
            } else {
                // Thanh toán thất bại
                Order order = orderService.updateOrderStatus(UUID.fromString(orderId), 3); // Status 3: payment failed
                response.sendRedirect("/api/order/pay");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
