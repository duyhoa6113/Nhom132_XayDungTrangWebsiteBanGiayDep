package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "HoaDonChiTiet", schema = "dbo",
        uniqueConstraints = @UniqueConstraint(name="UQ_HoaDon_Variant", columnNames = {"HoaDonId","VariantId"}),
        indexes = @Index(name="IX_HoaDonCT_Variant", columnList = "VariantId"))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class HoaDonChiTiet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HoaDonCTId")
    private Integer id;

    @ManyToOne(optional = false) @JoinColumn(name = "HoaDonId")
    private HoaDon hoaDon;

    @ManyToOne(optional = false) @JoinColumn(name = "VariantId")
    private SanPhamChiTiet variant;

    // snapshot thông tin SP – lấy theo DB, không join
    @Column(name = "TenSPSnapshot", length = 200, nullable = false)
    private String tenSPSnapshot;

    @Column(name = "MauSnapshot", length = 50)
    private String mauSnapshot;

    @Column(name = "SizeSnapshot", length = 50)
    private String sizeSnapshot;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "DonGia", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGia;

    @Column(name = "ThanhTien", precision = 18, scale = 2, insertable = false, updatable = false)
    private BigDecimal thanhTien; // computed column
}

