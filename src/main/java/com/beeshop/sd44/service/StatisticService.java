package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.response.StatisticResponse;
import com.beeshop.sd44.dto.response.TopProductResponse;
import com.beeshop.sd44.repository.OrderDetailRepo;
import com.beeshop.sd44.repository.OrderRepo;
import org.springframework.data.domain.PageRequest; // Cần thiết để lấy Top 5
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticService {
    private final OrderRepo orderRepo;
    private final OrderDetailRepo orderDetailRepo;

    public StatisticService(OrderRepo orderRepo, OrderDetailRepo orderDetailRepo) {
        this.orderRepo = orderRepo;
        this.orderDetailRepo = orderDetailRepo;
    }

    // Lấy thống kê tổng quan (Doanh thu, số đơn, sản phẩm bán ra)
    public StatisticResponse getGeneralStats() {
        Double revenue = orderRepo.getTotalRevenue(); //
        Long orders = orderRepo.countAllOrders();    //
        Long sold = orderDetailRepo.getTotalProductsSold(); //

        // Tránh lỗi Null nếu database chưa có dữ liệu
        return new StatisticResponse(
                revenue != null ? revenue : 0.0,
                orders != null ? orders : 0L,
                sold != null ? sold : 0L
        );
    }

    // Lấy danh sách 5 sản phẩm bán chạy nhất
    public List<TopProductResponse> getTop5Products() {
        // Gọi query và giới hạn lấy 5 bản ghi đầu tiên
        List<Object[]> results = orderDetailRepo.findTopProducts(PageRequest.of(0, 5));

        // Chuyển đổi dữ liệu thô (Object[]) sang DTO sạch sẽ
        return results.stream().map(row -> new TopProductResponse(
                (String) row[0],                  // Tên sản phẩm
                ((Number) row[1]).longValue(),    // Tổng số lượng bán
                ((Number) row[2]).doubleValue()   // Tổng doanh thu sản phẩm này
        )).collect(Collectors.toList());
    }
}