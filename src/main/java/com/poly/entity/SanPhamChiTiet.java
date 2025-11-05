package com.poly.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity cho bảng SanPhamChiTiet (Product Variants)
 * Mỗi variant là 1 tổ hợp của: SanPham + MauSac + KichThuoc
 * Mapping với database nhom132_shoponline
 */
@Entity
@Table(name = "SanPhamChiTiet")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"sanPham"}) // Tránh vòng lặp toString
@EqualsAndHashCode(exclude = {"sanPham"})
public class SanPhamChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VariantId")
    private Integer variantId;

    @Column(name = "SKU", nullable = false, unique = true, length = 64)
    private String sku;

    @Column(name = "Barcode", unique = true, length = 64)
    private String barcode;

    @Column(name = "GiaBan", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "GiaGoc", precision = 18, scale = 2)
    private BigDecimal giaGoc;

    @Column(name = "SoLuongTon", nullable = false)
    private Integer soLuongTon = 0;

    @Column(name = "HinhAnh", length = 512)
    private String hinhAnh;

    @Column(name = "TrangThai", nullable = false)
    private Integer trangThai = 1;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    // ========== RELATIONSHIPS ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SanPhamId", nullable = false)
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER vì cần thông tin màu sắc khi query
    @JoinColumn(name = "MauSacId", nullable = false)
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER vì cần thông tin kích thước khi query
    @JoinColumn(name = "KichThuocId", nullable = false)
    private KichThuoc kichThuoc;

    // ========== LIFECYCLE CALLBACKS ==========

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = 1;
        }
        if (soLuongTon == null) {
            soLuongTon = 0;
        }
    }

    // ========== BUSINESS METHODS ==========

    /**
     * Kiểm tra variant còn hàng không
     */
    public boolean isInStock() {
        return soLuongTon != null && soLuongTon > 0;
    }

    /**
     * Kiểm tra variant đang active không
     */
    public boolean isActive() {
        return trangThai != null && trangThai == 1;
    }

    /**
     * Tính tỷ lệ giảm giá (%)
     */
    public Integer getTyLeGiamGia() {
        if (giaGoc == null || giaGoc.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        if (giaBan == null || giaBan.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        BigDecimal giamGia = giaGoc.subtract(giaBan);
        return giamGia.divide(giaGoc, 2, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .intValue();
    }
}