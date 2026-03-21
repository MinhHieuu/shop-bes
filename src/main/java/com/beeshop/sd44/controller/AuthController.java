package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.LoginRequest;
import com.beeshop.sd44.dto.request.RefreshRequest;
import com.beeshop.sd44.dto.response.LoginResponse;
import com.beeshop.sd44.dto.response.UserResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.User;
import com.beeshop.sd44.service.AuthService;

import com.beeshop.sd44.service.CustomerService;
import com.beeshop.sd44.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final CustomerService customerService;
    public AuthController(AuthService authService, PasswordEncoder passwordEncoder, UserService userService, CustomerService customerService) {
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.userService = userService;
        this.customerService = customerService;
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws EntityNotFoundException {
        LoginResponse response = this.authService.login(loginRequest);
        if (response == null) {
            return ResponseEntity.status(400).body(new ApiResponse<>("sai email hoac mat khau", null));
        }

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + response.getAccessToken())
                .body(new ApiResponse<>("dang nhap thanh cong", response));
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        String newAccess = authService.refreshAccessToken(request.getRefreshToken());
        if (newAccess == null) {
            return ResponseEntity.status(401).body(new ApiResponse<>("refresh token khong hop le hoac het han", null));
        }
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + newAccess)
                .body(new ApiResponse<>("cap token moi thanh cong", newAccess));
    }

    @GetMapping("logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestBody RefreshRequest request) {
        boolean ok = authService.revokeRefreshToken(request.getRefreshToken());
        if (!ok) {
            return ResponseEntity.status(400).body(new ApiResponse<>("refresh token khong tim thay", null));
        }
        return ResponseEntity.ok().body(new ApiResponse<>("dang xuat thanh cong", null));
    }

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.isUserExit(user.getEmail(), user.getPhone())) {
            return ResponseEntity.status(409).body(new ApiResponse<>("email hoac sdt da duoc dang ky", null));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("user"); // Mặc định role là "user"
        user.setDeleteFlag(false);
        UserResponse response = this.userService.buildRespone(userService.createUser(user));
        return ResponseEntity.status(201).body(new ApiResponse<>("tao moi thanh cong", response));
    }

    @GetMapping("check-customer")
    public ResponseEntity<?> checkCustomer(@RequestParam String phone) {
        return ResponseEntity.ok().body(new ApiResponse<>("kiem tra thanh cong", customerService.checkCustomer(phone)));
    }
}
