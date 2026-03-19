package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.EmployeeOrderRequest;
import com.beeshop.sd44.dto.request.OrderFilterRequest;
import com.beeshop.sd44.dto.request.OrderRequest;
import com.beeshop.sd44.dto.request.ProductDetailRequest;
import com.beeshop.sd44.dto.response.OrderResponse;
import com.beeshop.sd44.dto.response.ProductDetailResponse;
import com.beeshop.sd44.dto.response.UserResponse;
import com.beeshop.sd44.entity.*;
import com.beeshop.sd44.repository.CartDetailRepo;
import com.beeshop.sd44.repository.CartRepo;
import com.beeshop.sd44.repository.OrderDetailRepo;
import com.beeshop.sd44.repository.OrderRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private static final int SHIPPING_FEE_DELIVERY = 30000;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepo orderRepo;
    private final OrderDetailRepo orderDetailRepo;
    private final UserService userService;
    private final CustomerService customerService;
    private final ProductDetailService productDetailService;
    private final VoucherService voucherService;
    private final CartDetailRepo cartDetailRepo;
    private final NotificationService notificationService;

    public OrderService(OrderRepo orderRepo, OrderDetailRepo orderDetailRepo, UserService userService,
            CustomerService customerService, ProductDetailService productDetailService,
            VoucherService voucherService, CartDetailRepo cartDetailRepo,
            NotificationService notificationService) {
        this.orderDetailRepo = orderDetailRepo;
        this.orderRepo = orderRepo;
        this.userService = userService;
        this.customerService = customerService;
        this.productDetailService = productDetailService;
        this.voucherService = voucherService;
        this.cartDetailRepo = cartDetailRepo;
        this.notificationService = notificationService;
    }

    /**
     * B3-B4: Đặt hàng online — role "user".
     * nguoi_dung_id = null (sẽ được set khi employee xác nhận).
     * khach_hang_id = Customer liên kết với user đặt hàng.
     */
    @Transactional
    public OrderResponse hanldePlaceOrder(OrderRequest orderRequest, UUID userID) {
        // 1. Tính tổng tiền hàng (subTotal)
        System.out.println("handlePlaceOrder - ship: " + orderRequest.isCounter());
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

        int shippingFee = orderRequest.isCounter() ? 0 : SHIPPING_FEE_DELIVERY;

        // 4. Tổng thanh toán
        double total = subTotal - discount + shippingFee;
        if (total < 0)
            total = 0;

        // 5. Tạo order
        Order order = new Order();
        // nguoi_dung_id = null — sẽ được set khi employee/admin xác nhận đơn
        // khach_hang_id = Customer liên kết với user đặt hàng
        Customer customer = customerService.getByUserId(userID);
        order.setCustomer(customer);
        order.setCreatedAt(new Date());
        order.setType(1); // 1 = online
        order.setPaymentDate(new Date());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setNote(orderRequest.getNote());
        order.setShippingFee(shippingFee);
        order.setTotal(total);
        order.setVoucher(voucher);
        order.setCode("HD" + String.format("%03d", orderRepo.count()));

        if ("COD".equals(orderRequest.getPaymentMethod())) {
            order.setPaymentStatus(0); // chưa thanh toán
            order.setStatus(0); // chờ xác nhận
        } else if ("VNPAY".equals(orderRequest.getPaymentMethod())) {
            order.setPaymentStatus(0); // đang thanh toán
            order.setStatus(0); // đã xác nhận
        }

        order = this.orderRepo.save(order);

        // 6. Tạo order detail + trừ tồn kho
        List<UUID> cartIdsToDelete = new ArrayList<>();
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
            cartIdsToDelete.add(productDetail.getId());
        }

        // 7. Xóa sp giỏ hàng
        cartDetailRepo.deleteByProductDetailIdIn(cartIdsToDelete);

        // 8. Bắn thông báo realtime — đơn hàng online mới
        final Order savedOrder = order;
        notificationService.createAndBroadcast(
                "Đơn hàng mới #" + savedOrder.getCode(),
                "Khách hàng vừa đặt đơn hàng online trị giá " + savedOrder.getTotal().longValue() + " đ",
                savedOrder.getId(),
                "NEW_ORDER");

        return buildOrderResponse(order, subTotal, discount);
    }

    @Transactional
    public OrderResponse handleCounterOrder(EmployeeOrderRequest orderRequest, UUID employeeId) {
        // 1. Tính tổng tiền hàng (subTotal) để check voucher
        double subTotal = 0;
        for (ProductDetailRequest pdRequest : orderRequest.getProductDetail()) {
            ProductDetail productDetail = productDetailService.getById(pdRequest.getId());
            subTotal += productDetail.getSalePrice() * pdRequest.getQuantity();
        }

        // 2. Áp dụng voucher nếu có
        Voucher voucher = null;
        double discount = 0;
        if (orderRequest.getVoucherCode() != null && !orderRequest.getVoucherCode().isBlank()) {
            voucher = voucherService.validateAndGet(orderRequest.getVoucherCode(), subTotal);
            discount = voucherService.calculateDiscount(voucher, subTotal);
        }

        // 3. Tính toán tổng tiền (có tính phí ship nếu là đơn giao hàng)
        // int shippingFee = (orderRequest.getType() != null && orderRequest.getType()
        // == 1) ? SHIPPING_FEE_DELIVERY : 0;
        double total = subTotal - discount;
        if (total < 0)
            total = 0;
        orderRequest.setTotal(total);

        // 4. Tạo Order
        Order order = createCounterOrder(orderRequest, employeeId);
        if (voucher != null) {
            order.setVoucher(voucher);
            order = orderRepo.save(order);
        }

        // 5. Lưu OrderDetail
        for (ProductDetailRequest pdRequest : orderRequest.getProductDetail()) {
            ProductDetail productDetail = productDetailService.getById(pdRequest.getId());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProductDetail(productDetail);
            orderDetail.setQuantity(pdRequest.getQuantity());
            orderDetail.setPrice(productDetail.getSalePrice());
            orderDetailRepo.save(orderDetail);
        }
        return buildOrderResponse(order, subTotal, discount);
    }

    public Order createCounterOrder(EmployeeOrderRequest orderRequest, UUID employeeId) {
        Order order = new Order();
        // Đơn tại quầy: nguoi_dung_id = employee/admin tạo đơn
        order.setUser(userService.getUserById(employeeId));

        // Tìm hoặc tạo mới khách hàng theo số điện thoại
        if (orderRequest.getPhoneNumber() != null && !orderRequest.getPhoneNumber().isBlank()) {
            Customer customer = customerService.findOrCreateBySdt(orderRequest.getPhoneNumber());
            order.setCustomer(customer);
        }

        order.setCreatedAt(new Date());
        Integer type = orderRequest.getType();
        if (type == null) {
            type = 0; // 0 = tại quầy (mặc định)
        }
        order.setType(type);
        order.setPaymentDate(new Date());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setNote(orderRequest.getNote());
        order.setTotal(orderRequest.getTotal());
        // order.setCode("HD" + order.getSum());
        if (type == 1) { // 1 = online
            order.setShippingFee(SHIPPING_FEE_DELIVERY);
            order.setTotal(order.getTotal() + SHIPPING_FEE_DELIVERY);
        } else { // 0 = tại quầy
            order.setShippingFee(0);
        }
        if ("CASH".equals(orderRequest.getPaymentMethod())) {
            order.setPaymentStatus(1); // đã thanh toán
            order.setStatus(1);
        } else if ("VNPAY".equals(orderRequest.getPaymentMethod())) {
            order.setPaymentStatus(0); // đang thanh toán
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

    public Order getMyOrderById(UUID orderId, UUID userId) {
        Customer customer = customerService.getByUserId(userId);
        if (customer == null) {
            return null;
        }

        return orderRepo.findByCustomerIdAndId(customer.getId(), orderId).orElse(null);

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
        // Chỉ set userResponse khi nguoi_dung_id != null
        if (order.getUser() != null) {
            response.setUserResponse(userService.buildRespone(userService.getUserById(order.getUser().getId())));
        }

        if (order.getCustomer() != null) {
            Customer c = order.getCustomer();
            UserResponse cr = new UserResponse();
            cr.setId(c.getId());
            cr.setName(c.getTen());
            cr.setPhone(c.getSdt());
            cr.setAddress(c.getDiaChi());
            if (c.getUser() != null) {
                cr.setEmail(c.getUser().getEmail());
                cr.setAvatar(c.getUser().getAvatar());
                cr.setRole(c.getUser().getRole());
            }
            response.setCustomerResponse(cr);
        }

        List<OrderDetail> odList = orderDetailRepo.getOrderDetailByOrder(order);
        List<ProductDetailResponse> listProduct = new ArrayList<>();
        for (OrderDetail orderDetail : odList) {
            ProductDetailResponse pdResponse = productDetailService.buildResponse(orderDetail.getProductDetail());
            pdResponse.setQuantityInOrder(orderDetail.getQuantity());
            listProduct.add(pdResponse);
        }
        response.setProductDetailResponses(listProduct);
        return response;
    }

    private OrderResponse buildOrderResponseWithoutDetail(Order order, double subTotal, double discount) {
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
        return response;
    }

    // Backward compatible
    private OrderResponse builresponse(Order order) {
        return buildOrderResponse(order, order.getTotal() != null ? order.getTotal() : 0, 0);
    }

    private OrderResponse builresponseWithoutDetail(Order order) {
        return buildOrderResponseWithoutDetail(order, order.getTotal() != null ? order.getTotal() : 0, 0);
    }

    public List<OrderResponse> getOrdersByUserId(UUID userId) {
        List<Order> orders = orderRepo.findByUserId(userId);
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(builresponse(order));
        }
        return responses;
    }

    /**
     * Lấy danh sách đơn hàng theo khach_hang_id — dùng cho role "user"
     */
    public List<OrderResponse> getOrdersByCustomerId(UUID customerId) {
        List<Order> orders = orderRepo.findByCustomerId(customerId);
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
            responses.add(builresponseWithoutDetail(order));
        }
        return responses;
    }

    public List<OrderResponse> getOrdersByFilter(OrderFilterRequest filter) {
        List<Order> orders = orderRepo.findOrdersByFilter(
                filter.getStatus(),
                filter.getPaymentStatus(),
                filter.getType(),
                filter.getPaymentMethod(),
                filter.getFromDate(),
                filter.getToDate());
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(builresponseWithoutDetail(order));
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

    /**
     * Cập nhật trạng thái đơn hàng.
     * Nếu status = 1 (đã xác nhận) và operatorId không null → set nguoi_dung_id =
     * người xác nhận.
     */
    public Order updateOrderStatus(UUID orderId, Integer status, UUID operatorId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            return null;
        }
        order.setStatus(status);
        // Khi xác nhận đơn (status = 1), ghi lại người xác nhận vào nguoi_dung_id
        if (status == 1 && operatorId != null) {
            order.setUser(userService.getUserById(operatorId));
        }
        return orderRepo.save(order);
    }

    /**
     * Backward compatibility cho các nơi gọi updateOrderStatus không truyền
     * operatorId
     */
    public Order updateOrderStatus(UUID orderId, Integer status) {
        return updateOrderStatus(orderId, status, null);
    }

    public void handleQuantity(Order order) {
        List<OrderDetail> list = orderDetailRepo.getOrderDetailByOrder(order);
        for (OrderDetail orderDetail : list) {
            productDetailService.updateQuantity(orderDetail.getProductDetail().getId(), order.getStatus(),
                    orderDetail.getQuantity());
        }
    }
}
