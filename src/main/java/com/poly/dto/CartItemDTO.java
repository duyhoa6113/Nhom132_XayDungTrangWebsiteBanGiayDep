package com.poly.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO cho mỗi item trong giỏ hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private Integer gioHangCTId;
    private Integer variantId;
    private String tenSanPham;
    private String sku;
    private String hinhAnh;
    private String mauSac;
    private String kichThuoc;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private Integer soLuongTon;
    private BigDecimal giaGoc;

    // Tính tỷ lệ giảm giá
    public Integer getTyLeGiamGia() {
        if (giaGoc == null || giaGoc.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        if (donGia == null || donGia.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        BigDecimal giamGia = giaGoc.subtract(donGia);
        return giamGia.divide(giaGoc, 2, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .intValue();
    }
}