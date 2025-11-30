package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO cho sản phẩm (quản lý admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDTO {
    private Integer sanPhamId;
    private String ten;
    private String moTa;
    private Integer trangThai;
    private Integer danhMucId;
    private String danhMucTen;
    private Integer thuongHieuId;
    private String thuongHieuTen;
    private Integer chatLieuId;
    private String chatLieuTen;
    private List<SanPhamChiTietDTO> variants = new ArrayList<>();
    
    // Thông tin bổ sung để hiển thị
    private String hinhAnhChinh;
    private Integer soLuongTon;
    private Double giaMin;
    private Integer soLuongDaBan;
}

