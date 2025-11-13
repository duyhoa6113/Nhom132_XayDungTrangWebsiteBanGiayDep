package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Integer id;
    private String maDonHang;
    private String hoTenNhan;
    private String sdtNhan;
    private String diaChiGiaoHang;
    private BigDecimal tongTienHang;
    private BigDecimal phiVanChuyen;
    private BigDecimal giamGiaKhuyenMai;
    private BigDecimal tongThanhToan;
    private String phuongThucThanhToan;
    private String trangThaiDonHang;
    private String trangThaiThanhToan;
    private String ghiChu;
    private LocalDateTime ngayDat;
    private List<OrderItemDTO> chiTietList;
}