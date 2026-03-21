package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.response.UserResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.User;
import com.beeshop.sd44.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private static final String DEFAULT_PASSWORD = "123456";
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        List<User> users;
        if ((keyword == null || keyword.isBlank()) && (role == null || role.isBlank())) {
            users = userService.getAllActiveUsers();
        } else {
            users = userService.searchUsers(keyword, role);
        }
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            responses.add(userService.buildRespone(user));
        }
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", responses));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") UUID id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", userService.buildRespone(user)));
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody User request) {
        if (request.getEmail() == null || request.getPhone() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("thieu thong tin", null));
        }
        if (userService.isUserExit(request.getEmail(), request.getPhone())) {
            return ResponseEntity.status(409).body(new ApiResponse<>("email hoac sdt da duoc dang ky", null));
        }
        if (request.getRole() == null) {
            request.setRole("employee");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        request.setDeleteFlag(false);
        User user = userService.createUser(request);
        return ResponseEntity.status(201).body(new ApiResponse<>("tao moi thanh cong", userService.buildRespone(user)));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable("id") UUID id,
                                                               @RequestBody User request) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        User updated = userService.updateUser(user);
        return ResponseEntity.ok(new ApiResponse<>("cap nhat thanh cong", userService.buildRespone(updated)));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable("id") UUID id) {
        boolean ok = userService.softDelete(id);
        if (!ok) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("xoa thanh cong", null));
    }

    @PutMapping("{id}/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@PathVariable("id") UUID id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay", null));
        }
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        userService.updateUser(user);
        return ResponseEntity.ok(new ApiResponse<>("dat lai mat khau thanh cong", null));
    }
}
