package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Entity cho bảng SanPhamChiTiet (Product Variant)
 *
 * @author Nhóm 132
 */
@Entity
@Table(name = "SanPhamChiTiet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VariantId")
    private Integer variantId;

    /**
     * Quan hệ Many-to-One với SanPham
     * Nhiều variant thuộc một sản phẩm
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SanPhamId", nullable = false)
    private SanPham sanPham;

    /**
     * Quan hệ Many-to-One với MauSac
     * Variant có một màu sắc
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MauSacId", nullable = false)
    private MauSac mauSac;

    /**
     * Quan hệ Many-to-One với KichThuoc
     * Variant có một kích thước
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "KichThuocId", nullable = false)
    private KichThuoc kichThuoc;

    /**
     * Quan hệ One-to-Many với GioHang
     * Một variant có thể có trong nhiều giỏ hàng
     */
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GioHang> gioHangs;

    /**
     * Quan hệ One-to-Many với HoaDonChiTiet
     * Một variant có thể có trong nhiều đơn hàng
     */
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDonChiTiet> hoaDonChiTiets;

    /**
     * Stock Keeping Unit - Mã định danh unique cho variant
     */
    @Column(name = "SKU", nullable = false, unique = true, length = 64)
    private String sku;

    /**
     * Mã vạch sản phẩm (optional)
     */
    @Column(name = "Barcode", unique = true, length = 64)
    private String barcode;

    /**
     * Giá bán hiện tại (sau giảm giá)
     */
    @Column(name = "GiaBan", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBan;

    /**
     * Giá gốc (trước giảm giá)
     */
    @Column(name = "GiaGoc", precision = 18, scale = 2)
    private BigDecimal giaGoc;

    /**
     * Số lượng tồn kho
     */
    @Column(name = "SoLuongTon", nullable = false)
    private Integer soLuongTon = 0;

    /**
     * URL hình ảnh của variant
     */
    @Column(name = "HinhAnh", length = 512)
    private String hinhAnh;

    /**
     * Trạng thái: 1 = Active, 0 = Inactive
     * TINYINT trong SQL Server → int trong Java
     */
    @Column(name = "TrangThai", nullable = false)
    private int trangThai = 1;

    /**
     * Thời gian tạo
     */
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật
     */
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // ============================================
    // LIFECYCLE CALLBACKS
    // ============================================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (trangThai == 0) {
            trangThai = 1; // Default active
        }
        if (soLuongTon == null) {
            soLuongTon = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ============================================
    // TRANSIENT METHODS
    // ============================================

    /**
     * Format giá bán hiển thị (VD: 1.500.000₫)
     */
    @Transient
    public String getGiaBanFormatted() {
        if (giaBan == null) return "0₫";
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(giaBan) + "₫";
    }

    /**
     * Format giá gốc hiển thị
     */
    @Transient
    public String getGiaGocFormatted() {
        if (giaGoc == null) return null;
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(giaGoc) + "₫";
    }

    /**
     * Tính % giảm giá
     * ✅ ĐÃ SỬA - Fix lỗi BigDecimal to double conversion
     */
    @Transient
    public Integer getTyLeGiamGia() {
        if (giaGoc == null || giaGoc.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        if (giaBan.compareTo(giaGoc) >= 0) {
            return 0;
        }
        // Tính discount
        BigDecimal discount = giaGoc.subtract(giaBan);

        // Tính % = (discount / giaGoc) * 100
        BigDecimal percentage = discount
                .multiply(BigDecimal.valueOf(100))
                .divide(giaGoc, 2, RoundingMode.HALF_UP);  // ✅ FIX: Dùng RoundingMode thay vì deprecated constant

        return percentage.intValue();
    }

    /**
     * Kiểm tra còn hàng
     */
    @Transient
    public boolean isInStock() {
        return soLuongTon != null && soLuongTon > 0;
    }

    /**
     * Kiểm tra active
     */
    @Transient
    public boolean isActive() {
        return trangThai == 1;
    }

    /**
     * Lấy tên đầy đủ của variant
     * VD: "Nike Air Max - Đỏ - 42"
     */
    @Transient
    public String getTenDayDu() {
        StringBuilder sb = new StringBuilder();
        if (sanPham != null) {
            sb.append(sanPham.getTen());
        }
        if (mauSac != null) {
            sb.append(" - ").append(mauSac.getTen());
        }
        if (kichThuoc != null) {
            sb.append(" - ").append(kichThuoc.getTen());
        }
        return sb.toString();
    }

    /**
     * Lấy tên màu sắc
     */
    @Transient
    public String getTenMauSac() {
        return mauSac != null ? mauSac.getTen() : "";
    }

    /**
     * Lấy tên kích thước
     */
    @Transient
    public String getTenKichThuoc() {
        return kichThuoc != null ? kichThuoc.getTen() : "";
    }

    /**
     * Lấy mã hex màu sắc
     */
    @Transient
    public String getMaHexMauSac() {
        return mauSac != null ? mauSac.getMaHex() : "";
    }

    // ============================================
    // EQUALS AND HASHCODE
    // ============================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPhamChiTiet that = (SanPhamChiTiet) o;
        return variantId != null && variantId.equals(that.variantId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // ============================================
    // TO STRING
    // ============================================

    @Override
    public String toString() {
        return "SanPhamChiTiet{" +
                "variantId=" + variantId +
                ", sku='" + sku + '\'' +
                ", giaBan=" + giaBan +
                ", soLuongTon=" + soLuongTon +
                ", trangThai=" + trangThai +
                '}';
    }
}