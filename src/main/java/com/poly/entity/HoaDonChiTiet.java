package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(name = "HoaDonChiTiet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HoaDonChiTietId")
    private Integer hoaDonChiTietId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HoaDonId", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VariantId", nullable = false)
    private SanPhamChiTiet variant;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "DonGia", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "ThanhTien", nullable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // ==================== TRANSIENT FIELDS ====================

    /**
     * Lấy tên sản phẩm
     */
    @Transient
    public String getTenSanPham() {
        return variant != null && variant.getSanPham() != null ?
                variant.getSanPham().getTen() : "";
    }

    /**
     * Lấy hình ảnh
     */
    @Transient
    public String getHinhAnh() {
        return variant != null ? variant.getHinhAnh() : null;
    }

    /**
     * Lấy màu sắc
     */
    @Transient
    public String getTenMauSac() {
        return variant != null ? variant.getTenMauSac() : "";
    }

    /**
     * Lấy kích thước
     */
    @Transient
    public String getTenKichThuoc() {
        return variant != null ? variant.getTenKichThuoc() : "";
    }

    /**
     * Format đơn giá
     */
    @Transient
    public String getDonGiaFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(donGia);
    }

    /**
     * Format thành tiền
     */
    @Transient
    public String getThanhTienFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(thanhTien);
    }
}
