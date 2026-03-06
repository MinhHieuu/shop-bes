package com.beeshop.sd44.controller;

import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.Marterial;
import com.beeshop.sd44.entity.Brand;
import com.beeshop.sd44.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class BrandController {
    @Autowired
    private BrandService service;
    @GetMapping("thuong-hieu")
    public ResponseEntity<?> getAll(Model model){
        List<Brand> list = this.service.getAll();
        return ResponseEntity.ok().body(new ApiResponse<>("lay thanh cong", list));
    }

    @PostMapping("thuong-hieu")
    public ResponseEntity<?> create(@Valid @RequestBody Brand brand, BindingResult result) {
        boolean exitsThuongHieu = this.service.isNameExit(brand.getName());
        if(exitsThuongHieu == true) {
            return ResponseEntity.status(409).body(new ApiResponse<Marterial>("da ton tai", null));
        }
        if(result.hasErrors()) {
            String error = result.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse<>(error, null));
        }
        this.service.hanldeSave(brand);
        return ResponseEntity.status(201).body(new ApiResponse<>("tao moi thanh cong", brand));
    }

    @DeleteMapping("thuong-hieu/{id}")
    public ResponseEntity<?> delete (@PathVariable("id") UUID id) {
        Brand brand = this.service.getById(id);
        if(brand == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("khong tim thay", null));
        }
        this.service.handleDelete(brand);
        return ResponseEntity.ok().body(new ApiResponse<>("xoa thanh cong", null));
    }

    @PutMapping("thuong-hieu/{id}")
    public ResponseEntity<?> update (@PathVariable("id")UUID id,
                                     @Valid @RequestBody Brand newBrand, BindingResult result) {
        Brand brand = this.service.getById(id);
        if (brand == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        if (result.hasErrors()) {
            return ResponseEntity.status(400).body(new ApiResponse<>(result.getFieldError().getDefaultMessage(), null));
        }
        brand.setName(newBrand.getName());
        this.service.hanldeSave(brand);
        return ResponseEntity.ok().body(new ApiResponse<>("cap nhat thanh cong", brand));
    }
}
