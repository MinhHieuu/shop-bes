package com.beeshop.sd44.dto.response;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class TopProductResponse {
    private String name;           // Tên biến thể sản phẩm
    private Long totalSold;        // Tổng số lượng đã bán
    private Double totalRevenue;   // Doanh thu từ sản phẩm này
}
