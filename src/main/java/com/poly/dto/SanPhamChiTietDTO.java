package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho sản phẩm chi tiết (variant)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamChiTietDTO {
    private Integer variantId;
    private Integer sanPhamId;
    private Integer mauSacId;
    private String mauSacTen;
    private String mauSacMaHex;
    private Integer kichThuocId;
    private String kichThuocTen;
    private String sku;
    private String barcode;
    private BigDecimal giaBan;
    private BigDecimal giaGoc;
    private Integer soLuongTon;
    private String hinhAnh;
    private Integer trangThai;
}

