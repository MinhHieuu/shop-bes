package com.beeshop.sd44.dto.response;

import java.util.List;

public class StatisticResponse {
    private List<DailyRevenue> revenueOverTime;
    private List<LowStockProduct> lowStockProducts;
    private List<BestSellingProduct> bestSellingProducts;
    private Long totalOrders;

    public StatisticResponse() {
    }

    public StatisticResponse(List<DailyRevenue> revenueOverTime, List<LowStockProduct> lowStockProducts, List<BestSellingProduct> bestSellingProducts, Long totalOrders) {
        this.revenueOverTime = revenueOverTime;
        this.lowStockProducts = lowStockProducts;
        this.bestSellingProducts = bestSellingProducts;
        this.totalOrders = totalOrders;
    }

    public List<DailyRevenue> getRevenueOverTime() {
        return revenueOverTime;
    }

    public void setRevenueOverTime(List<DailyRevenue> revenueOverTime) {
        this.revenueOverTime = revenueOverTime;
    }

    public List<LowStockProduct> getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(List<LowStockProduct> lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public List<BestSellingProduct> getBestSellingProducts() {
        return bestSellingProducts;
    }

    public void setBestSellingProducts(List<BestSellingProduct> bestSellingProducts) {
        this.bestSellingProducts = bestSellingProducts;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }
}
