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
    private final VoucherService voucherService;

    public OrderService(OrderRepo orderRepo, OrderDetailRepo orderDetailRepo, UserService userService,
            ProductDetailService productDetailService, VoucherService voucherService) {
        this.orderDetailRepo = orderDetailRepo;
        this.orderRepo = orderRepo;
        this.userService = userService;
        this.productDetailService = productDetailService;
        this.voucherService = voucherService;
    }

    /**
     * B3-B4: Đặt hàng online — BE tính toán toàn bộ tiền.
     */
    public OrderResponse hanldePlaceOrder(OrderRequest orderRequest, UUID userID) {
        if (orderRequest.getProductDetail() == null || orderRequest.getProductDetail().isEmpty()) {
            throw new IllegalArgumentException("Gio hang trong, khong the dat hang");
        }
        if (orderRequest.getPaymentMethod() == null || orderRequest.getPaymentMethod().isBlank()) {
            throw new IllegalArgumentException("Phuong thuc thanh toan khong duoc de trong");
        }

        // 1. Tính tổng tiền hàng (subTotal) từ DB — KHÔNG tin FE
        double subTotal = 0;
        for (ProductDetailRequest pdRequest : orderRequest.getProductDetail()) {
            ProductDetail productDetail = productDetailService.getById(pdRequest.getId());
            if (productDetail == null) {
                throw new IllegalArgumentException("San pham khong ton tai: " + pdRequest.getId());
            }
            if (pdRequest.getQuantity() == null || pdRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("So luong phai lon hon 0");
            }
            if (pdRequest.getQuantity() > productDetail.getQuantity()) {
                throw new IllegalArgumentException("San pham " + productDetail.getName()
                        + " chi con " + productDetail.getQuantity() + " san pham");
            }
            subTotal += productDetail.getSalePrice() * pdRequest.getQuantity();
        }

        // 2. Áp dụng voucher nếu có
        Voucher voucher = null;
        double discount = 0;
        if (orderRequest.getVoucherCode() != null && !orderRequest.getVoucherCode().isBlank()) {
            voucher = voucherService.validateAndGet(orderRequest.getVoucherCode(), subTotal);
            discount = voucherService.calculateDiscount(voucher, subTotal);
        }

        // 3. Phí ship (online luôn có phí ship)
        int shippingFee = SHIPPING_FEE_DELIVERY;

        // 4. Tổng thanh toán
        double total = subTotal - discount + shippingFee;
        if (total < 0)
            total = 0;

        // 5. Tạo order
        Order order = new Order();
        order.setUser(userService.getUserById(userID));
        order.setCreatedAt(new Date());
        order.setType(1); // online
        order.setPaymentDate(new Date());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setNote(orderRequest.getNote());
        order.setShippingFee(shippingFee);
        order.setTotal(total);
        order.setVoucher(voucher);
        order.setCode("HD" + order.getSum());

        if ("COD".equals(orderRequest.getPaymentMethod())) {
            order.setPaymentStatus(0); // chưa thanh toán
            order.setStatus(0); // chờ xác nhận
        } else if ("Online".equals(orderRequest.getPaymentMethod())) {
            order.setPaymentStatus(0); // đang thanh toán
            order.setStatus(1); // đã xác nhận
        }

        order = this.orderRepo.save(order);

        // 6. Tạo order detail + trừ tồn kho
        for (ProductDetailRequest pdRequest : orderRequest.getProductDetail()) {
            ProductDetail productDetail = productDetailService.getById(pdRequest.getId());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProductDetail(productDetail);
            orderDetail.setQuantity(pdRequest.getQuantity());
            orderDetail.setPrice(productDetail.getSalePrice());
            orderDetailRepo.save(orderDetail);
            // Trừ tồn kho
            productDetail.setQuantity(productDetail.getQuantity() - pdRequest.getQuantity());
        }

        return buildOrderResponse(order, subTotal, discount);
    }

    public OrderResponse handleCounterOrder(EmployeeOrderRequest orderRequest, UUID employeeId) {
        Order order = createCounterOrder(orderRequest, employeeId);
        double subTotal = 0;
        for (ProductDetailRequest pdRequest : orderRequest.getProductDetail()) {
            ProductDetail productDetail = productDetailService.getById(pdRequest.getId());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProductDetail(productDetail);
            orderDetail.setQuantity(pdRequest.getQuantity());
            orderDetail.setPrice(productDetail.getSalePrice());
            orderDetailRepo.save(orderDetail);
            subTotal += productDetail.getSalePrice() * pdRequest.getQuantity();
        }
        return buildOrderResponse(order, subTotal, 0);
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

    private OrderResponse buildOrderResponse(Order order, double subTotal, double discount) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId().toString());
        response.setCode(order.getCode());
        response.setNote(order.getNote());
        response.setPaymentDate(order.getPaymentDate());
        response.setCreatedAt(order.getCreatedAt());
        response.setShippingFee(order.getShippingFee());
        response.setSubTotal(subTotal);
        response.setDiscount(discount);
        response.setTotal(order.getTotal());
        response.setType(order.getType());
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        if (order.getVoucher() != null) {
            response.setVoucherCode(order.getVoucher().getMa());
        }
        response.setUserResponse(userService.buildRespone(userService.getUserById(order.getUser().getId())));
        List<OrderDetail> odList = orderDetailRepo.getOrderDetailByOrder(order);
        List<ProductDetailResponse> listProduct = new ArrayList<>();
        for (OrderDetail orderDetail : odList) {
            listProduct.add(productDetailService.buildResponse(orderDetail.getProductDetail()));
        }
        response.setProductDetailResponses(listProduct);
        return response;
    }

    // Backward compatible — các nơi khác gọi builresponse(order) cũ
    private OrderResponse builresponse(Order order) {
        return buildOrderResponse(order, order.getTotal() != null ? order.getTotal() : 0, 0);
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
        for (OrderDetail orderDetail : list) {
            productDetailService.updateQuantity(orderDetail.getProductDetail().getId(), order.getStatus(), orderDetail.getQuantity());
        }
    }
}
