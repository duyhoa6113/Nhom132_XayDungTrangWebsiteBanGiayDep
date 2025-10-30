package com.poly.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor  // Needed for @Builder
@Builder
public class ProductHomeDTO {
    private Integer sanPhamId;
    private String ten;
    private String moTa;
    private String danhMucTen;
    private String thuongHieuTen;
    private BigDecimal giaMin;
    private BigDecimal giaMax;
    private BigDecimal giaGocMin;
    private String hinhAnhChinh;
    private Long soLuongTon;  // Changed to Long for SUM aggregate
    private Integer danhMucId;

    // Tính phần trăm giảm giá
    public Integer getTyLeGiamGia() {
        if (giaGocMin != null && giaMin != null && giaGocMin.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal giamGia = giaGocMin.subtract(giaMin);
            BigDecimal tyLe = giamGia.divide(giaGocMin, 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100));
            return tyLe.intValue();
        }
        return 0;
    }

    // Format giá tiền
    public String getGiaMinFormatted() {
        if (giaMin != null) {
            return String.format("%,.0f", giaMin);
        }
        return "0";
    }

    public String getGiaGocMinFormatted() {
        if (giaGocMin != null) {
            return String.format("%,.0f", giaGocMin);
        }
        return "0";
    }
}