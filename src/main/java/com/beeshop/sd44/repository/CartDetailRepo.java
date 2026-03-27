package com.beeshop.sd44.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.beeshop.sd44.entity.CartDetail;
import com.beeshop.sd44.entity.Cart;
import com.beeshop.sd44.entity.ProductDetail;

@Repository
public interface CartDetailRepo extends JpaRepository<CartDetail, UUID> {
    List<CartDetail> findByCart(Cart cart);
    Optional<CartDetail> findByCartIdAndProductDetailId(UUID cart, UUID productId);

    void deleteByProductDetailIdIn(List<UUID> listId);
    Optional<CartDetail> findByOrderIdAndProductId(UUID orderId, UUID productId);
}
