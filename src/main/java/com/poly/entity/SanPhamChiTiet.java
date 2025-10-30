package com.poly.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SanPhamChiTiet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VariantId")
    private Integer variantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SanPhamId", nullable = false)
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MauSacId", nullable = false)
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KichThuocId", nullable = false)
    private KichThuoc kichThuoc;

    @Column(name = "SKU", length = 64, nullable = false, unique = true)
    private String sku;

    @Column(name = "Barcode", length = 64, unique = true)
    private String barcode;

    @Column(name = "GiaBan", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaBan;

    @Column(name = "GiaGoc", precision = 18, scale = 2)
    private BigDecimal giaGoc;

    @Column(name = "SoLuongTon", nullable = false)
    private Integer soLuongTon = 0;

    @Column(name = "HinhAnh", length = 512)
    private String hinhAnh;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai = 1;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}