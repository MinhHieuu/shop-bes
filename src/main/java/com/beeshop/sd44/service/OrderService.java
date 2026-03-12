package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.EmployeeOrderRequest;
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
    private static final int SHIPPING_FEE_DELIVERY = 30000;
    private final OrderRepo orderRepo;
    private final OrderDetailRepo orderDetailRepo;
    private final UserService userService;
    private final ProductDetailService productDetailService;

    public OrderService(OrderRepo orderRepo, OrderDetailRepo orderDetailRepo, UserService userService,
            ProductDetailService productDetailService) {
        this.orderDetailRepo = orderDetailRepo;
        this.orderRepo = orderRepo;
        this.userService = userService;
        this.productDetailService = productDetailService;
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

    public OrderResponse handleCounterOrder(EmployeeOrderRequest orderRequest, UUID employeeId) {
        Order order = createCounterOrder(orderRequest, employeeId);
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

    public Order createCounterOrder(EmployeeOrderRequest orderRequest, UUID employeeId) {
        Order order = new Order();
        order.setUser(userService.getUserById(employeeId));
        order.setCreatedAt(new Date());
        Integer type = orderRequest.getType();
        if (type == null) {
            type = 1;
        }
        order.setType(type);
        order.setPaymentDate(new Date());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setNote(orderRequest.getNote());
        order.setTotal(orderRequest.getTotal());
        order.setCode("HD" + order.getSum());
        if (type == 2) {
            order.setShippingFee(SHIPPING_FEE_DELIVERY);
        } else {
            order.setShippingFee(0);
        }
        if ("CASH".equals(orderRequest.getPaymentMethod())) {
            order.setPaymentStatus(1);
            order.setStatus(1);
        } else if ("Online".equals(orderRequest.getPaymentMethod())) {
            order.setPaymentStatus(0);
            order.setStatus(1);
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
        response.setType(order.getType());
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

    public List<OrderResponse> getOrdersByUserId(UUID userId) {
        List<Order> orders = orderRepo.findByUserId(userId);
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(builresponse(order));
        }
        return responses;
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepo.findAllByOrderByCreatedAtDesc();
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(builresponse(order));
        }
        return responses;
    }

    public OrderResponse getOrderResponseById(UUID orderId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            return null;
        }
        return builresponse(order);
    }

    public Order updateOrderStatus(UUID orderId, Integer status) {
        Order order = getOrderById(orderId);
        if (order == null) {
            return null;
        }
        order.setStatus(status);
        return orderRepo.save(order);
    }

    public void handleQuantity(Order order) {
        List<OrderDetail> list = orderDetailRepo.getOrderDetailByOrder(order);
        for(OrderDetail orderDetail : list) {
            productDetailService.updateQuantity(orderDetail.getProductDetail().getId(), order.getStatus(), orderDetail.getQuantity());
        }
    }

}
