package com.beeshop.sd44.dto.response;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class StatisticResponse {
    private Double totalRevenue;    // Tổng doanh thu từ Order.total
    private Long totalOrders;       // Tổng số đơn hàng
    private Long productsSold;      // Tổng sản phẩm đã bán từ OrderDetail.quantity
}