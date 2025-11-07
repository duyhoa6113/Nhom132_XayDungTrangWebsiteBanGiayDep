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
@Table(name = "GioHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GioHangId")
    private Integer gioHangId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KhachHangId", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VariantId", nullable = false)
    private SanPhamChiTiet variant;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Tính thành tiền
     */
    @Transient
    public BigDecimal getThanhTien() {
        if (variant != null && variant.getGiaBan() != null && soLuong != null) {
            return variant.getGiaBan().multiply(new BigDecimal(soLuong));
        }
        return BigDecimal.ZERO;
    }

    /**
     * Format thành tiền
     */
    @Transient
    public String getThanhTienFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(getThanhTien());
    }



    /**
     * Lấy hình ảnh
     */
    @Transient
    public String getHinhAnh() {
        return variant != null ? variant.getHinhAnh() : null;
    }
}
