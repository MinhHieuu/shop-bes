package com.beeshop.sd44.controller;

import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.Marterial;
import com.beeshop.sd44.service.MarterialService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class MarterialController {
    private final MarterialService marterialService;

    public MarterialController(MarterialService marterialService) {
        this.marterialService = marterialService;
    }

    @GetMapping("chat-lieu")
    public ResponseEntity<?> getAll(Model model){
       List<Marterial> list = this.marterialService.getAll();
        return ResponseEntity.ok().body(new ApiResponse<>("lay thanh cong", list));
    }

    @PostMapping("chat-lieu")
    public ResponseEntity<ApiResponse<Marterial>> create(@Valid @RequestBody Marterial marterial, BindingResult result) {
        boolean exitsChatLieu = this.marterialService.isNameExit(marterial.getName());
        if(exitsChatLieu == true) {
            return ResponseEntity.status(409).body(new ApiResponse<Marterial>("da ton tai", null));
        }
        if(result.hasErrors()) {
            String error = result.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse<>(error, null));
        }
        this.marterialService.hanldeSave(marterial);
        return ResponseEntity.status(201).body(new ApiResponse<>("tao moi thanh cong", marterial));
    }

    @DeleteMapping("chat-lieu/{id}")
    public ResponseEntity<?> delete (@PathVariable("id")UUID id) {
        Marterial marterial = this.marterialService.getById(id);
        if(marterial == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("khong tim thay", null));
        }
        this.marterialService.handleDelete(marterial);
        return ResponseEntity.ok().body(new ApiResponse<>("xoa thanh cong", null));
    }

    @PutMapping("chat-lieu/{id}")
    public ResponseEntity<?> update (@PathVariable("id")UUID id,
                                     @Valid @RequestBody Marterial newMarterial, BindingResult result) {
        Marterial marterial = this.marterialService.getById(id);
        if(marterial == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        if(result.hasErrors()) {
            return ResponseEntity.status(400).body(new ApiResponse<>(result.getFieldError().getDefaultMessage(), null));
        }
        marterial.setName(newMarterial.getName());
        this.marterialService.hanldeSave(marterial);
        return ResponseEntity.ok().body(new ApiResponse<>("cap nhat thanh cong", marterial));
    }
}
