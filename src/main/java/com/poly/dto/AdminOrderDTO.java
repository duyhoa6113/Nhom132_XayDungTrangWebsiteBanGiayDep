package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho quản lý đơn hàng trong admin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderDTO {
    private Integer id;
    private String maHoaDon;
    private String hoTenKhachHang;
    private String emailKhachHang;
    private String hoTenNhan;
    private String sdtNhan;
    private String diaChiNhan;
    private String phuongXa;
    private String quanHuyen;
    private String tinhTP;
    private BigDecimal tongTien;
    private BigDecimal giamGia;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongThanhToan;
    private String phuongThucThanhToan;
    private String trangThai;
    private String trangThaiThanhToan;
    private String ghiChu;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> chiTietList;
}

