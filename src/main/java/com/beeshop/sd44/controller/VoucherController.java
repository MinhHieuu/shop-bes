package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.VoucherRequest;
import com.beeshop.sd44.dto.response.VoucherResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.service.VoucherService;
import jakarta.validation.Valid;
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

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai) {

        List<VoucherResponse> responses;

        if ((keyword == null || keyword.isBlank()) && trangThai == null) {
            responses = voucherService.getAll();
        } else {
            responses = voucherService.search(keyword, trangThai);
        }

        return ResponseEntity.ok(new ApiResponse<>("Lấy danh sách voucher thành công", responses));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getById(@PathVariable UUID id) {
        try {
            VoucherResponse response = voucherService.getResponseById(id);
            return ResponseEntity.ok(new ApiResponse<>("Lấy voucher thành công", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<VoucherResponse>> create(@Valid @RequestBody VoucherRequest request) {
        try {
            VoucherResponse response = voucherService.create(request);
            return ResponseEntity.status(201).body(new ApiResponse<>("Tạo voucher thành công", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> update(@PathVariable UUID id,
                                                               @Valid @RequestBody VoucherRequest request) {
        try {
            VoucherResponse response = voucherService.update(id, request);
            return ResponseEntity.ok(new ApiResponse<>("Cập nhật voucher thành công", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Object>> deactivate(@PathVariable UUID id) {
        try {
            voucherService.deactivate(id);
            return ResponseEntity.ok(new ApiResponse<>("Ngừng hoạt động voucher thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(e.getMessage(), null));
        }
    }
}