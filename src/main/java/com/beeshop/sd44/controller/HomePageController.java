package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.response.CartDetailResponse;
import com.beeshop.sd44.dto.response.ProductDetailResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.service.CartService;
import com.beeshop.sd44.service.ProductDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.beeshop.sd44.entity.Cart;

import java.util.List;
import java.util.UUID;

@RestController
public class HomePageController {
    private final ProductDetailService productDetailService;
    private final CartService cartService;

    public HomePageController(ProductDetailService productDetailService, CartService cartService) {
        this.productDetailService = productDetailService;
        this.cartService = cartService;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<ProductDetailResponse>>> getHomePage() {
        List<ProductDetailResponse> list = this.productDetailService.getListDetail(false);
        return ResponseEntity.ok().body(new ApiResponse<>("lay thanh cong", list));
    }

    @GetMapping("cart")
    public ResponseEntity<ApiResponse<List<CartDetailResponse>>> getCart(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Cart cart = cartService.getCartByUserId(userId);
        List<CartDetailResponse> cartDetails = cartService.getCartDetailByCart(cart);
        return ResponseEntity.ok().body(new ApiResponse<>("lay thanh cong", cartDetails));
    }

    @GetMapping("add-product-to-cart/{productId}")
    public ResponseEntity<?> handleAddToCart(@PathVariable("productId") UUID productDetailId, Authentication authentication) {
        String userId = authentication.getName();
        cartService.addProductToCart(productDetailId, UUID.fromString(userId));
        return ResponseEntity.ok().body(new ApiResponse<>("them thanh cong", null));
    }

    @DeleteMapping("remove-product-from-cart/{cartDetailId}")
    public ResponseEntity<?> handleRemoveFromCart(@PathVariable("cartDetailId") UUID cartDetailId, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        cartService.removeProductFromCart(cartDetailId, userId);
        return ResponseEntity.ok().body(new ApiResponse<>("xoa thanh cong", null));
    }


}
