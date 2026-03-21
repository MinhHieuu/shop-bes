package com.beeshop.sd44.controller;

import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.Size;
import com.beeshop.sd44.service.SizeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class SizeController {
    private final SizeService service;
    public SizeController(SizeService service) {
        this.service = service;
    }
    @GetMapping("size")
    public ResponseEntity<?> getAll(){
        List<Size> list = this.service.getAll();
        return ResponseEntity.ok().body(new ApiResponse<>("lay thanh cong", list));
    }

    @PostMapping("size")
    public ResponseEntity<?> create(@Valid @RequestBody Size size) {
        boolean exitsSize = this.service.isNameExit(size.getName());
        if(exitsSize) {
            return ResponseEntity.status(409).body(new ApiResponse<>("da ton tai", null));
        }
        this.service.hanldeSave(size);
        return ResponseEntity.status(201).body(new ApiResponse<>("tao moi thanh cong", size));
    }

    @DeleteMapping("size/{id}")
    public ResponseEntity<?> delete (@PathVariable("id") UUID id) {
        Size size = this.service.getById(id);
        if(size == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("khong tim thay", null));
        }
        this.service.handleDelete(size);
        return ResponseEntity.ok().body(new ApiResponse<>("xoa thanh cong", null));
    }

    @PutMapping("size/{id}")
    public ResponseEntity<?> update (@PathVariable("id")UUID id,
                                     @Valid @RequestBody Size newSize) {
        Size size = this.service.getById(id);
        if(size == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        size.setName(newSize.getName());
        this.service.hanldeSave(size);
        return ResponseEntity.ok().body(new ApiResponse<>("cap nhat thanh cong", size));
    }
}
