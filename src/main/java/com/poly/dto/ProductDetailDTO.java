package com.poly.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO cho trang chi tiết sản phẩm
 * Mapping với database nhom132_shoponline
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {

    // ============ THÔNG TIN SẢN PHẨM CHÍNH ============
    private Integer sanPhamId;
    private String ten;
    private String moTa;

    // ============ QUAN HỆ VỚI CÁC BẢNG KHÁC ============
    // DanhMuc
    private Integer danhMucId;
    private String danhMucTen;

    // ThuongHieu
    private Integer thuongHieuId;
    private String thuongHieuTen;

    // ChatLieu
    private Integer chatLieuId;
    private String chatLieuTen;

    // ============ HÌNH ẢNH ============
    private String hinhAnhChinh;        // Ảnh chính từ variant đầu tiên
    private List<String> hinhAnhPhu;    // Danh sách ảnh từ các variants khác

    // ============ GIÁ CẢ VÀ KHUYẾN MÃI ============
    private BigDecimal giaMin;          // Giá thấp nhất trong các variants
    private BigDecimal giaMax;          // Giá cao nhất trong các variants
    private BigDecimal giaGocMin;       // Giá gốc thấp nhất
    private BigDecimal giaGocMax;       // Giá gốc cao nhất

    // Formatted prices (đã format sẵn để hiển thị)
    private String giaMinFormatted;     // VD: "2,890,000"
    private String giaMaxFormatted;
    private String giaGocMinFormatted;
    private String giaGocMaxFormatted;

    // Tính toán giảm giá
    private Integer tyLeGiamGia;        // % giảm giá (0-100)
    private String tienTietKiem;        // Số tiền tiết kiệm được

    // ============ TỒN KHO ============
    private Integer soLuongTon;         // Tổng số lượng tồn kho của tất cả variants

    // ============ CHI TIẾT VARIANTS (MÀU SẮC, KÍCH THƯỚC) ============
    private List<VariantDTO> variants;  // Danh sách tất cả variants của sản phẩm
    private List<ColorDTO> colors;      // Danh sách màu sắc có sẵn
    private List<SizeDTO> sizes;        // Danh sách kích thước có sẵn

    // ============ NESTED CLASSES ============

    /**
     * DTO cho từng variant cụ thể (SanPhamChiTiet)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantDTO {
        private Integer variantId;          // VariantId từ SanPhamChiTiet
        private String sku;
        private String barcode;

        // Giá
        private BigDecimal giaBan;
        private BigDecimal giaGoc;
        private String giaBanFormatted;
        private String giaGocFormatted;

        // Tồn kho
        private Integer soLuongTon;

        // Hình ảnh
        private String hinhAnh;

        // Màu sắc
        private Integer mauSacId;
        private String mauSacTen;

        // Kích thước
        private Integer kichThuocId;
        private String kichThuocTen;
    }

    /**
     * DTO cho danh sách màu sắc
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColorDTO {
        private Integer mauSacId;
        private String ten;
        private Boolean available;      // Có sẵn hàng hay không
    }

    /**
     * DTO cho danh sách kích thước
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SizeDTO {
        private Integer kichThuocId;
        private String ten;
        private Boolean available;      // Có sẵn hàng hay không
    }
}