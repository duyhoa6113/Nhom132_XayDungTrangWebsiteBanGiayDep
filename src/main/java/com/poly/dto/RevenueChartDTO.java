package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho dữ liệu biểu đồ doanh thu theo tháng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueChartDTO {
    private String month; // Tên tháng (Jan, Feb, etc.)
    private String monthName; // Tên tháng đầy đủ (January, February, etc.)
    private BigDecimal revenue; // Doanh thu của tháng
    private int year; // Năm
    private int monthNumber; // Số tháng (1-12)
}

