package com.beeshop.sd44.repository;

import com.beeshop.sd44.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> getUserByEmail(String email);
    Boolean existsByEmailOrPhone(String email, String phone);
    List<User> findAllByDeleteFlag(Boolean deleteFlag);
}
