package com.beeshop.sd44.repository;

import com.beeshop.sd44.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> getUserByEmail(String email);
    Boolean existsByEmailOrPhone(String email, String phone);
    List<User> findAllByDeleteFlag(Boolean deleteFlag);

    @Query("SELECT u FROM User u WHERE u.deleteFlag = false " +
           "AND (:role IS NULL OR u.role = :role) " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.address) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchUsers(@Param("keyword") String keyword, @Param("role") String role);
}
