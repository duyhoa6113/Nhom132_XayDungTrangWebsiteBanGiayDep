package com.poly.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO cho tổng kết giỏ hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryDTO {

    private Integer gioHangId;
    private List<CartItemDTO> items;
    private Integer tongSoLuong;
    private BigDecimal tongTien;
    private BigDecimal tongTietKiem;

    // Constructor tính toán tự động
    public static CartSummaryDTO fromItems(Integer gioHangId, List<CartItemDTO> items) {
        Integer tongSoLuong = items.stream()
                .mapToInt(CartItemDTO::getSoLuong)
                .sum();

        BigDecimal tongTien = items.stream()
                .map(CartItemDTO::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tongTietKiem = items.stream()
                .filter(item -> item.getGiaGoc() != null)
                .map(item -> {
                    BigDecimal giamGia = item.getGiaGoc().subtract(item.getDonGia());
                    return giamGia.multiply(BigDecimal.valueOf(item.getSoLuong()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartSummaryDTO.builder()
                .gioHangId(gioHangId)
                .items(items)
                .tongSoLuong(tongSoLuong)
                .tongTien(tongTien)
                .tongTietKiem(tongTietKiem)
                .build();
    }
}