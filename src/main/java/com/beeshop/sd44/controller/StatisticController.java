package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.response.StatisticResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/statistics")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("/admin-dashboard")
    public ResponseEntity<ApiResponse<StatisticResponse>> getDashboardStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        
        StatisticResponse stats = statisticService.getAdminDashboardStatistics(fromDate, toDate);
        return ResponseEntity.ok(new ApiResponse<>("Lấy thống kê thành công", stats));
    }
}
