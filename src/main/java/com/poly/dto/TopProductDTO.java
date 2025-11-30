package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho sản phẩm bán chạy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDTO {
    private Integer sanPhamId;
    private String ten;
    private Long soLuongBan; // Số lượng đã bán (tổng từ các đơn hàng)
    private Integer rank; // Xếp hạng (1-10)
}

