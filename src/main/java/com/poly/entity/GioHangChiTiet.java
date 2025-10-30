package com.poly.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "GioHangChiTiet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHangChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GioHangCTId")
    private Integer gioHangCTId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GioHangId", nullable = false)
    private GioHang gioHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VariantId", nullable = false)
    private SanPhamChiTiet variant;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "DonGia", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGia;

    // Computed column - don't set manually
    @Column(name = "ThanhTien", precision = 18, scale = 2, insertable = false, updatable = false)
    private BigDecimal thanhTien;
}