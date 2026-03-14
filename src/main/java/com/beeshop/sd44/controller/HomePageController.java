package com.beeshop.sd44.controller;

import ch.qos.logback.core.util.StringUtil;
import com.beeshop.sd44.dto.response.CartDetailResponse;
import com.beeshop.sd44.dto.response.ProductDetailResponse;
import com.beeshop.sd44.dto.response.ProductSale;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.service.CartService;
import com.beeshop.sd44.service.ProductDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping("/sale")
    public ResponseEntity<ApiResponse<List<ProductSale>>> getHomePageSale(@RequestParam(required = false) String id) {
        List<ProductSale> list;
        if(StringUtil.isNullOrEmpty(id)){
            list = this.productDetailService.getListSaler(null);
        }else{
            list = this.productDetailService.getListSaler(id);
        }
        return ResponseEntity.ok().body(new ApiResponse<>("lay thanh cong", list));
    }
    @GetMapping("/sale/{id}")
    public ResponseEntity<ApiResponse<List<ProductSale>>> getHomePageSaleByProductId(@PathVariable("id") String id) {
        List<ProductSale> list = this.productDetailService.getListSalerByProductId(id);
        return ResponseEntity.ok().body(new ApiResponse<>("lay thanh cong", list));
    }


    // B2: Lấy giỏ hàng
    @GetMapping("cart")
    public ResponseEntity<ApiResponse<List<CartDetailResponse>>> getCart(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Cart cart = cartService.getCartByUserId(userId);
        List<CartDetailResponse> cartDetails = cartService.getCartDetailByCart(cart);
        return ResponseEntity.ok().body(new ApiResponse<>("lay thanh cong", cartDetails));
    }

    // B1: Thêm sản phẩm vào giỏ
    @PostMapping("add-product-to-cart/{productId}")
    public ResponseEntity<?> handleAddToCart(@PathVariable("productId") UUID productDetailId, Authentication authentication) {
        String userId = authentication.getName();
        cartService.addProductToCart(productDetailId, UUID.fromString(userId));
        return ResponseEntity.ok().body(new ApiResponse<>("them thanh cong", null));
    }

    // B2: Cập nhật số lượng sản phẩm trong giỏ
    @PutMapping("cart/{cartDetailId}")
    public ResponseEntity<?> updateCartQuantity(@PathVariable("cartDetailId") UUID cartDetailId,
                                                @RequestParam int quantity,
                                                Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        CartDetailResponse response = cartService.updateQuantity(cartDetailId, quantity, userId);
        if (response == null) {
            return ResponseEntity.ok().body(new ApiResponse<>("da xoa san pham khoi gio hang", null));
        }
        return ResponseEntity.ok().body(new ApiResponse<>("cap nhat thanh cong", response));
    }

    // B2: Xóa 1 sản phẩm khỏi giỏ
    @DeleteMapping("remove-product-from-cart/{cartDetailId}")
    public ResponseEntity<?> handleRemoveFromCart(@PathVariable("cartDetailId") UUID cartDetailId, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        cartService.removeProductFromCart(cartDetailId, userId);
        return ResponseEntity.ok().body(new ApiResponse<>("xoa thanh cong", null));
    }

    // B2: Clear toàn bộ giỏ hàng
    @DeleteMapping("cart/clear")
    public ResponseEntity<?> clearCart(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        cartService.clearCart(userId);
        return ResponseEntity.ok().body(new ApiResponse<>("xoa gio hang thanh cong", null));
    }
}
