package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.OrderRequest;
import com.beeshop.sd44.dto.response.OrderResponse;
import com.beeshop.sd44.dto.response.VNPayResponse;
import com.beeshop.sd44.dto.response.VoucherApplyResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.Customer;
import com.beeshop.sd44.entity.Order;
import com.beeshop.sd44.entity.Voucher;
import com.beeshop.sd44.service.CustomerService;
import com.beeshop.sd44.service.InvoicePdfService;
import com.beeshop.sd44.service.NotificationService;
import com.beeshop.sd44.service.OrderService;
import com.beeshop.sd44.service.VNPayService;
import com.beeshop.sd44.service.VoucherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;
    private final VNPayService vnPayService;
    private final VoucherService voucherService;
    private final InvoicePdfService invoicePdfService;
    private final CustomerService customerService;
    private final NotificationService notificationService;

    public OrderController(OrderService orderService, VNPayService vnPayService,
            VoucherService voucherService, InvoicePdfService invoicePdfService,
            CustomerService customerService, NotificationService notificationService) {
        this.orderService = orderService;
        this.vnPayService = vnPayService;
        this.voucherService = voucherService;
        this.invoicePdfService = invoicePdfService;
        this.customerService = customerService;
        this.notificationService = notificationService;
    }

    /**
     * B3: Kiểm tra mã giảm giá — gọi ở trang pre-order.
     * GET /api/order/check-voucher?code=ABC123&subTotal=500000
     */
    @GetMapping("check-voucher")
    public ResponseEntity<ApiResponse<VoucherApplyResponse>> checkVoucher(
            @RequestParam String code,
            @RequestParam double subTotal) {
        Voucher voucher = voucherService.validateAndGet(code, subTotal);
        double discount = voucherService.calculateDiscount(voucher, subTotal);

        VoucherApplyResponse resp = new VoucherApplyResponse();
        resp.setMa(voucher.getMa());
        resp.setTen(voucher.getTen());
        resp.setLoaiGiam(voucher.getLoaiGiam());
        resp.setGiaTriGiam(voucher.getGiaTriGiam());
        resp.setDiscountAmount(discount);
        resp.setSubTotal(subTotal);
        resp.setTotalAfterDiscount(subTotal - discount);
        return ResponseEntity.ok(new ApiResponse<>("ap dung ma giam gia thanh cong", resp));
    }

    @PostMapping("pay")
    public ResponseEntity<ApiResponse<Object>> handleOrder(@Valid @RequestBody OrderRequest orderRequset,
            Authentication authentication, HttpServletRequest request) throws ServletException, IOException {
        UUID userId = UUID.fromString(authentication.getName());
        OrderResponse orderResponse = orderService.hanldePlaceOrder(orderRequset, userId);
        if ("COD".equals(orderRequset.getPaymentMethod())) {
            return ResponseEntity.ok().body(new ApiResponse<>("tao don hang thanh cong", orderResponse));
        } else {
            // Dùng total từ BE (đã tính sẵn), không dùng total từ FE
            VNPayResponse vnPayResponse = this.vnPayService.createPaymentLink(orderResponse.getId(),
                    orderResponse.getTotal().longValue(),
                    "Thanh toan don hang : " + orderResponse.getCode(), request);
            if (vnPayResponse == null || !vnPayResponse.isSuccess()) {
                return ResponseEntity.internalServerError()
                        .body(new ApiResponse<>("tao link thanh toan that bai", null));
            }
            return ResponseEntity.ok().body(new ApiResponse<>("tao link thanh toan thanh cong", vnPayResponse));
        }
    }

    @GetMapping("vnpay-return")
    public void handleVNPayReturn(@RequestParam Map<String, String> params, HttpServletResponse response)
            throws IOException {
        System.out.println("VNPay return received: " + params);
        // Verify signature
        if (!vnPayService.verifyPayment(params)) {
            response.sendError(400, "Chữ ký không hợp lệ");
            return;
        }

        String orderId = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        if ("00".equals(responseCode)) {
            // Thanh toán thành công
            orderService.updatePaymentStatus(UUID.fromString(orderId), 1);

            // Bắn thông báo VNPAY thanh toán thành công
            Order paidOrder = orderService.getOrderById(UUID.fromString(orderId));
            if (paidOrder != null) {
                notificationService.createAndBroadcast(
                        "Thanh toán VNPAY thành công",
                        "Đơn hàng #" + paidOrder.getCode() + " đã được thanh toán qua VNPAY",
                        paidOrder.getId(),
                        "VNPAY_SUCCESS");
            }
            response.sendRedirect("http://localhost:3000/order/success");
        } else if ("24".equals(responseCode)) {
            // Người dùng hủy thanh toán tại VNPAY -> đánh dấu đơn là đã hủy
            // paymentStatus = 3 (đã hủy), status = 3 (hủy)
            orderService.updatePaymentStatus(UUID.fromString(orderId), 2);
            orderService.updateOrderStatus(UUID.fromString(orderId), 3);
            response.sendRedirect("http://localhost:3000/order/confirm");
        } else {
            // Thanh toán thất bại
            orderService.updatePaymentStatus(UUID.fromString(orderId), 2);
            orderService.updateOrderStatus(UUID.fromString(orderId), 3);
            response.sendRedirect("http://localhost:3000/order/error");
        }
    }

    /**
     * In hóa đơn PDF theo orderId.
     * GET /api/order/{id}/invoice
     * Trả về file PDF với header Content-Disposition: attachment
     */
    @GetMapping("{id}/invoice")
    public ResponseEntity<byte[]> printInvoice(@PathVariable UUID id) throws IOException {
        byte[] pdfBytes = invoicePdfService.generateInvoicePdf(id);
        if (pdfBytes == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice-" + id + ".pdf");
        headers.setContentLength(pdfBytes.length);
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * Lấy danh sách đơn hàng của user đang đăng nhập (role=user).
     * GET /api/order/my-orders
     * Dùng khach_hang_id để tìm kiếm thay vì nguoi_dung_id.
     */
    @GetMapping("my-orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Customer customer = customerService.getByUserId(userId);
        if (customer == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay thong tin khach hang", null));
        }
        List<OrderResponse> orders = orderService.getOrdersByCustomerId(customer.getId());
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", orders));
    }

}
