package com.poly.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    /**
     * Helper method để convert trạng thái sang tiếng Việt
     */
    public String getTrangThaiText() {
        if (trangThai == null) return "";
        return switch (trangThai) {
            case "ChoXuLy" -> "Chờ xử lý";
            case "DaXacNhan" -> "Đã xác nhận";
            case "DangChuanBi" -> "Đang chuẩn bị";
            case "DangGiao" -> "Đang giao";
            case "DaGiao" -> "Đã giao";
            case "HoanThanh" -> "Hoàn thành";
            case "DaHuy" -> "Đã hủy";
            default -> trangThai;
        };
    }
}

