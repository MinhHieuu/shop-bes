package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.OrderRequest;
import com.beeshop.sd44.dto.response.OrderResponse;
import com.beeshop.sd44.dto.response.VNPayResponse;
import com.beeshop.sd44.dto.response.VoucherApplyResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.Voucher;
import com.beeshop.sd44.service.InvoicePdfService;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;
    private final VNPayService vnPayService;
    private final VoucherService voucherService;
    private final InvoicePdfService invoicePdfService;

    public OrderController(OrderService orderService, VNPayService vnPayService,
                           VoucherService voucherService, InvoicePdfService invoicePdfService) {
        this.orderService = orderService;
        this.vnPayService = vnPayService;
        this.voucherService = voucherService;
        this.invoicePdfService = invoicePdfService;
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
    public void  handleVNPayReturn(@RequestParam Map<String, String> params, HttpServletResponse response)
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
            response.sendRedirect("http://localhost:3000/order-success");
        } else {
            // Thanh toán thất bại
            orderService.updatePaymentStatus(UUID.fromString(orderId), 3);
            response.sendRedirect("http://localhost:3000/order-error");
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

}
