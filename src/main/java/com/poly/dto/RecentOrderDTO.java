package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho đơn hàng gần đây
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentOrderDTO {
    private Integer hoaDonId;
    private String maHoaDon;
    private String hoTenKhachHang;
    private BigDecimal tongThanhToan;
    private LocalDateTime createdAt;
    private String trangThai;
    private String trangThaiThanhToan;
}

