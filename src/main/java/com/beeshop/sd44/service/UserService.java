package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.response.UserResponse;
import com.beeshop.sd44.entity.User;
import com.beeshop.sd44.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepo userRepo;
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User getByEmail(String email) {
        Optional<User> user = userRepo.getUserByEmail(email);
        if(user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public User createUser(User user) {
       return this.userRepo.save(user);
    }

    public Boolean isUserExit(String email, String phone) {
        return this.userRepo.existsByEmailOrPhone(email, phone);
    }

    public User getUserById(UUID id) {
        Optional<User> user = userRepo.findById(id);
        if(user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public List<User> getAllActiveUsers() {
        return userRepo.findAllByDeleteFlag(false);
    }

    public User updateUser(User user) {
        return userRepo.save(user);
    }

    public boolean softDelete(UUID id) {
        User user = getUserById(id);
        if (user == null) {
            return false;
        }
        user.setDeleteFlag(true);
        userRepo.save(user);
        return true;
    }

    public User updateProfile(UUID id, User update) {
        User user = getUserById(id);
        if (user == null) {
            return null;
        }
        user.setName(update.getName());
        user.setPhone(update.getPhone());
        user.setAddress(update.getAddress());
        user.setAvatar(update.getAvatar());
        return userRepo.save(user);
    }

    public UserResponse buildRespone(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setRole(user.getRole());
        return response;
    }
}
