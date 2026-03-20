package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.response.StatisticResponse;
import com.beeshop.sd44.repository.OrderRepo;
import com.beeshop.sd44.repository.ProductDetailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StatisticService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductDetailRepo productDetailRepo;

    public StatisticResponse getAdminDashboardStatistics(Date fromDate, Date toDate) {
        StatisticResponse response = new StatisticResponse();
        response.setRevenueOverTime(orderRepo.getRevenueByDate(fromDate, toDate));
        response.setLowStockProducts(productDetailRepo.getLowStockProducts(10));
        response.setBestSellingProducts(orderRepo.getBestSellingProducts(fromDate, toDate));
        response.setTotalOrders(orderRepo.getTotalOrders(fromDate, toDate));
        return response;
    }
}
