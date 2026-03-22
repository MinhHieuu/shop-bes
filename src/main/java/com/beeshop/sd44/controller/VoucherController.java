package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.VoucherRequest;
import com.beeshop.sd44.dto.response.VoucherResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai
    ) {
        List<VoucherResponse> data;

        if ((keyword == null || keyword.trim().isEmpty()) && trangThai == null) {
            data = voucherService.getAll();
        } else {
            data = voucherService.search(keyword, trangThai);
        }

        return ResponseEntity.ok(
                new ApiResponse<>("Lấy danh sách voucher thành công", data)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getById(@PathVariable UUID id) {
        try {
            VoucherResponse data = voucherService.getResponseById(id);
            return ResponseEntity.ok(
                    new ApiResponse<>("Lấy voucher thành công", data)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VoucherResponse>> create(@Valid @RequestBody VoucherRequest request) {
        try {
            VoucherResponse data = voucherService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Tạo voucher thành công", data));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody VoucherRequest request
    ) {
        try {
            VoucherResponse data = voucherService.update(id, request);
            return ResponseEntity.ok(
                    new ApiResponse<>("Cập nhật voucher thành công", data)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id) {
        try {
            voucherService.deactivate(id);
            return ResponseEntity.ok(
                    new ApiResponse<>("Ngừng hoạt động voucher thành công", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> getActiveVouchers() {
        List<VoucherResponse> data = voucherService.getActiveVouchers();
        return ResponseEntity.ok(
                new ApiResponse<>("Lấy danh sách voucher đang hoạt động thành công", data)
        );
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> getAvailableVouchersForOrder(
            @RequestParam double subTotal
    ) {
        List<VoucherResponse> data = voucherService.getAvailableVouchersForOrder(subTotal);
        return ResponseEntity.ok(
                new ApiResponse<>("Lấy danh sách voucher áp dụng được thành công", data)
        );
    }

    @GetMapping("/best")
    public ResponseEntity<ApiResponse<VoucherResponse>> getBestVoucherForOrder(
            @RequestParam double subTotal
    ) {
        VoucherResponse data = voucherService.getBestVoucherForOrder(subTotal);
        return ResponseEntity.ok(
                new ApiResponse<>("Lấy voucher tốt nhất thành công", data)
        );
    }

    @GetMapping("/{id}/usage-count")
    public ResponseEntity<ApiResponse<Integer>> getUsageCount(@PathVariable UUID id) {
        try {
            Integer data = voucherService.getUsageCount(id);
            return ResponseEntity.ok(
                    new ApiResponse<>("Lấy số lần sử dụng voucher thành công", data)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @GetMapping("/{id}/display-status")
    public ResponseEntity<ApiResponse<String>> getDisplayStatus(@PathVariable UUID id) {
        try {
            String data = voucherService.getVoucherDisplayStatus(id);
            return ResponseEntity.ok(
                    new ApiResponse<>("Lấy trạng thái hiển thị voucher thành công", data)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @GetMapping("/{id}/real-status")
    public ResponseEntity<ApiResponse<Integer>> getRealStatus(@PathVariable UUID id) {
        try {
            Integer data = voucherService.getVoucherRealStatus(id);
            return ResponseEntity.ok(
                    new ApiResponse<>("Lấy trạng thái thực tế voucher thành công", data)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<VoucherResponse>> validateVoucher(
            @RequestParam String code,
            @RequestParam double subTotal
    ) {
        try {
            var voucher = voucherService.validateAndGet(code, subTotal);
            VoucherResponse data = voucherService.getResponseById(voucher.getId());

            return ResponseEntity.ok(
                    new ApiResponse<>("Voucher hợp lệ", data)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }
}