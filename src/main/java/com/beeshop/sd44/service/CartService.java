package com.beeshop.sd44.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.beeshop.sd44.dto.response.CartDetailResponse;
import com.beeshop.sd44.entity.Cart;
import com.beeshop.sd44.entity.CartDetail;
import com.beeshop.sd44.entity.ProductDetail;
import com.beeshop.sd44.entity.User;
import com.beeshop.sd44.repository.CartDetailRepo;
import com.beeshop.sd44.repository.CartRepo;

@Service
public class CartService {
    private final CartRepo cartRepo;
    private final CartDetailRepo cartDetailRepo;
    private final ProductDetailService productDetailService;

    public CartService(CartRepo cartRepo, CartDetailRepo cartDetailRepo, ProductDetailService productDetailService) {
        this.cartRepo = cartRepo;
        this.cartDetailRepo = cartDetailRepo;
        this.productDetailService = productDetailService;
    }

    public Cart getCartByUserId(UUID userId) {
        Optional<Cart> cart = cartRepo.findByUser_Id(userId);
        if (cart.isPresent()) {
            return cart.get();
        } else {
            Cart newCart = new Cart();
            newCart.setUser(new User(userId));
            newCart.setSum(0);
            return cartRepo.save(newCart);
        }
    }

    public CartDetail saveCartDetail(CartDetail cartDetail) {
        return cartDetailRepo.save(cartDetail);
    }

    // B1: Thêm vào giỏ — nếu đã có thì +1 quantity
    public void addProductToCart(UUID productDetailId, UUID userId, Integer quantity) {
        Cart cart = getCartByUserId(userId);

        ProductDetail productDetail = productDetailService.getById(productDetailId);
        if (productDetail == null) {
            throw new IllegalArgumentException("San pham khong ton tai");
        }
        // Kiểm tra sản phẩm đã có trong giỏ chưa
        Optional<CartDetail> existing = cartDetailRepo.findByCartIdAndProductDetailId(cart.getId(), productDetail.getId());
        if (existing.isPresent()) {
            CartDetail cartDetail = existing.get();
            cartDetail.setQuantity(cartDetail.getQuantity() + quantity);
            saveCartDetail(cartDetail);
        } else {
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCart(cart);
            cartDetail.setProductDetail(productDetail);
            cartDetail.setQuantity(quantity);
            cartDetail.setPrice(productDetail.getSalePrice());
            saveCartDetail(cartDetail);
        }
        System.out.println("a");
    }

    // B2: Cập nhật số lượng sản phẩm trong giỏ
    public CartDetailResponse updateQuantity(UUID cartDetailId, int quantity, UUID userId) {
        Cart cart = getCartByUserId(userId);
        CartDetail cartDetail = cartDetailRepo.findById(cartDetailId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay san pham trong gio hang"));
        if (!cartDetail.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("San pham khong thuoc gio hang cua ban");
        }
        if (quantity <= 0) {
            cartDetailRepo.delete(cartDetail);
            return null;
        }
        // Kiểm tra không vượt quá tồn kho
        if (quantity > cartDetail.getProductDetail().getQuantity()) {
            throw new IllegalArgumentException("So luong vuot qua ton kho (con lai: "
                    + cartDetail.getProductDetail().getQuantity() + ")");
        }
        cartDetail.setQuantity(quantity);
        return buildResponse(saveCartDetail(cartDetail));
    }

    public void removeProductFromCart(UUID cartDetailId, UUID userId) {
        Cart cart = getCartByUserId(userId);
        CartDetail cartDetail = cartDetailRepo.findById(cartDetailId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay san pham trong gio hang"));
        if (!cartDetail.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("San pham khong thuoc gio hang cua ban");
        }
        cartDetailRepo.delete(cartDetail);
    }

    // B2: Clear toàn bộ giỏ hàng
    public void clearCart(UUID userId) {
        Cart cart = getCartByUserId(userId);
        List<CartDetail> cartDetails = cartDetailRepo.findByCart(cart);
        cartDetailRepo.deleteAll(cartDetails);
    }

    public List<CartDetailResponse> getCartDetailByCart(Cart cart) {
        List<CartDetailResponse> responses = new ArrayList<>();
        List<CartDetail> cartDetails = cartDetailRepo.findByCart(cart);
        for (CartDetail cartDetail : cartDetails) {
            responses.add(buildResponse(cartDetail));
        }
        return responses;
    }

    public CartDetailResponse buildResponse(CartDetail cartDetail) {
        CartDetailResponse response = new CartDetailResponse();
        response.setId(cartDetail.getId());
        response.setProductDetail(productDetailService.buildResponse(cartDetail.getProductDetail()));
        response.setQuantity(cartDetail.getQuantity());
        response.setTotalPrice(cartDetail.getPrice() * cartDetail.getQuantity());
        return response;
    }
}
