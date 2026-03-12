package com.beeshop.sd44.controller;

import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.Color;
import com.beeshop.sd44.service.ColorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class ColorController {
    @Autowired
    private ColorService service;

    @GetMapping("mau-sac")
    public ResponseEntity<?> getAll() {
        List<Color> list = this.service.getAll();
        return ResponseEntity.ok().body(new ApiResponse<>("lay thanh cong", list));
    }

    @PostMapping("mau-sac")
    public ResponseEntity<?> create(@Valid @RequestBody Color color) {
        boolean exitsMau = this.service.isNameExit(color.getName());
        if (exitsMau) {
            return ResponseEntity.status(409).body(new ApiResponse<>("da ton tai", null));
        }
        this.service.hanldeSave(color);
        return ResponseEntity.status(201).body(new ApiResponse<>("tao moi thanh cong", color));
    }

    @DeleteMapping("mau-sac/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        Color color = this.service.getById(id);
        if (color == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("khong tim thay", null));
        }
        this.service.handleDelete(color);
        return ResponseEntity.ok().body(new ApiResponse<>("xoa thanh cong", null));
    }

    @PutMapping("mau-sac/{id}")
    public ResponseEntity<?> update(@PathVariable("id") UUID id,
            @Valid @RequestBody Color newColor) {
        Color color = this.service.getById(id);
        if (color == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        color.setName(newColor.getName());
        this.service.hanldeSave(color);
        return ResponseEntity.ok().body(new ApiResponse<>("cap nhat thanh cong", color));
    }
}
