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

    public void addProductToCart(UUID productDetailId, UUID userId) {
        Cart cart = getCartByUserId(userId);
        ProductDetail productDetail = productDetailService.getById(productDetailId);
        CartDetail cartDetail = new CartDetail();
        cartDetail.setCart(cart);
        cartDetail.setProductDetail(productDetail);
        cartDetail.setQuantity(1);
        cartDetail.setPrice(productDetail.getSalePrice());
        saveCartDetail(cartDetail);
    }

    public void removeProductFromCart(UUID cartDetailId, UUID userId) {
        Cart cart = getCartByUserId(userId);
        CartDetail cartDetail = cartDetailRepo.findById(cartDetailId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay san pham trong gio hang"));
        if (!cartDetail.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("San pham khong thuoc gio hang cua ban");
        }
        cartDetailRepo.delete(cartDetail);
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
        response.setProductDetail(productDetailService.buildResponse(cartDetail.getProductDetail()));
        response.setQuantity(cartDetail.getQuantity());
        response.setTotalPrice(cartDetail.getPrice() * cartDetail.getQuantity());
        return response;
    }

}
