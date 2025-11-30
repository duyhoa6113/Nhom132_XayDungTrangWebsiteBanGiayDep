package com.poly.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho tổng quan thống kê dashboard
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private BigDecimal totalRevenue; // Tổng doanh thu tháng hiện tại
    private BigDecimal previousMonthRevenue; // Doanh thu tháng trước
    private Long totalOrders; // Tổng số đơn hàng
    private Long totalProducts; // Tổng số sản phẩm
    private List<RevenueChartDTO> revenueChart; // Dữ liệu biểu đồ 12 tháng
    private List<TopProductDTO> topProducts; // Top 10 sản phẩm bán chạy
    private List<RecentOrderDTO> recentOrders; // 5 đơn hàng gần đây
}

