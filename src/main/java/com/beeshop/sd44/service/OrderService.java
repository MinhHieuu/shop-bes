package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.OrderRequest;
import com.beeshop.sd44.dto.request.ProductDetailRequest;
import com.beeshop.sd44.dto.response.OrderResponse;
import com.beeshop.sd44.dto.response.ProductDetailResponse;
import com.beeshop.sd44.entity.*;
import com.beeshop.sd44.repository.OrderDetailRepo;
import com.beeshop.sd44.repository.OrderRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final OrderDetailRepo orderDetailRepo;
    private final UserService userService;
    private final ProductDetailService productDetailService;
    private final CartService cartService;

    public OrderService(OrderRepo orderRepo, OrderDetailRepo orderDetailRepo, UserService userService,
            ProductDetailService productDetailService, CartService cartService) {
        this.orderDetailRepo = orderDetailRepo;
        this.orderRepo = orderRepo;
        this.userService = userService;
        this.productDetailService = productDetailService;
        this.cartService = cartService;
    }

    public OrderResponse hanldePlaceOrder(OrderRequest orderRequest, UUID userID) {
        Order order = createOrder(orderRequest, userID);
        for (ProductDetailRequest pdRequest : orderRequest.getProductDetail()) {
            ProductDetail productDetail = productDetailService.getById(pdRequest.getId());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProductDetail(productDetail);
            orderDetail.setQuantity(pdRequest.getQuantity());
            orderDetail.setPrice(productDetail.getSalePrice());
            orderDetailRepo.save(orderDetail);
        }
        return builresponse(order);
    }

    public Order createOrder(OrderRequest orderRequest, UUID userId) {
        Order order = new Order();
        order.setUser(userService.getUserById(userId));
        order.setCreatedAt(new Date());
        order.setType(1);
        order.setPaymentDate(new Date());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setNote(orderRequest.getNote());
        order.setTotal(orderRequest.getTotal());
        order.setCode("HD" + order.getSum());
        if (orderRequest.getPaymentMethod().equals("COD")) {
            order.setPaymentStatus(1); // đã thanh toán
            order.setStatus(0); // chờ xác nhận
        } else if (orderRequest.getPaymentMethod().equals("Online")) {
            order.setPaymentStatus(0); // đang thanh toán
            order.setStatus(1);// đã xác nhận
        }
        return this.orderRepo.save(order);
    }

    public Order updatePaymentStatus(UUID orderId, Integer paymentStatus) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.setPaymentStatus(paymentStatus);
            order.setPaymentDate(new Date());
            return orderRepo.save(order);
        }
        return null;
    }

    public Order getOrderById(UUID orderId) {
        return orderRepo.findById(orderId).orElse(null);
    }

    private OrderResponse builresponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId().toString());
        response.setCode(order.getCode());
        response.setNote(order.getNote());
        response.setPaymentDate(order.getPaymentDate());
        response.setCreatedAt(order.getCreatedAt());
        response.setShippingFee(order.getShippingFee());
        response.setTotal(order.getTotal());
        response.setStatus(order.getStatus());
        response.setUserResponse(userService.buildRespone(userService.getUserById(order.getUser().getId())));
        List<OrderDetail> odList = orderDetailRepo.getOrderDetailByOrder(order);
        List<ProductDetailResponse> listProduct = new ArrayList<>();
        for (OrderDetail orderDetail : odList) {
            listProduct.add(productDetailService.buildResponse(orderDetail.getProductDetail()));
        }
        response.setProductDetailResponses(listProduct);
        response.setPaymentMethod(order.getPaymentMethod());
        return response;
    }

    public void handleQuantity(Order order) {
        List<OrderDetail> list = orderDetailRepo.getOrderDetailByOrder(order);
        for(OrderDetail orderDetail : list) {
            productDetailService.updateQuantity(orderDetail.getProductDetail().getId(), order.getStatus(), orderDetail.getQuantity());
        }
    }

}
