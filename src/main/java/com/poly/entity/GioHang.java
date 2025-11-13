package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "KhachHangId", nullable = false)
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "VariantId", nullable = false)
    private SanPhamChiTiet variant;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Calculated field
    @Transient
    public BigDecimal getTongTien() {
        if (variant != null && variant.getGiaBan() != null && soLuong != null) {
            return variant.getGiaBan().multiply(BigDecimal.valueOf(soLuong));
        }
        return BigDecimal.ZERO;
    }

    @Transient
    public BigDecimal getGiaTaiThoidiem() {
        return variant != null ? variant.getGiaBan() : BigDecimal.ZERO;
    }
}