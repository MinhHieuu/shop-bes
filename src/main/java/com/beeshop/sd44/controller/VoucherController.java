package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.VoucherRequest;
import com.beeshop.sd44.dto.response.VoucherResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.service.VoucherService;
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
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", voucherService.getAll()));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getById(@PathVariable("id") UUID id) {
        VoucherResponse response = voucherService.getResponseById(id);
        if (response == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", response));
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<VoucherResponse>> create(@RequestBody VoucherRequest request) {
        VoucherResponse response = voucherService.create(request);
        return ResponseEntity.status(201).body(new ApiResponse<>("tao moi thanh cong", response));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> update(@PathVariable("id") UUID id,
                                                               @RequestBody VoucherRequest request) {
        VoucherResponse response = voucherService.update(id, request);
        if (response == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("cap nhat thanh cong", response));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Object>> deactivate(@PathVariable("id") UUID id) {
        voucherService.deactivate(id);
        return ResponseEntity.ok(new ApiResponse<>("xoa thanh cong", null));
    }
}
