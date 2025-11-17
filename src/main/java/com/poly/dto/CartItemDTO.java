package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Integer id;
    private Integer variantId;
    private String tenSanPham;
    private String hinhAnh;
    private String mauSac;
    private String kichThuoc;
    private Integer soLuong;
    private Integer maxStock;
    private BigDecimal gia;
    private BigDecimal giaGoc;
    private BigDecimal tongTien;
    private String sku;
}