package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "HoaDon", schema = "dbo",
        indexes = @Index(name="IX_HoaDon_KhachHang", columnList = "KhachHangId"))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class HoaDon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HoaDonId")
    private Integer id;

    @ManyToOne(optional = false) @JoinColumn(name = "KhachHangId")
    private KhachHang khachHang;

    @ManyToOne @JoinColumn(name = "NhanVienId")
    private NhanVien nhanVien;

    @ManyToOne @JoinColumn(name = "KhuyenMaiId")
    private KhuyenMai khuyenMaiDon;

    @Column(name = "HoTenNhan", length = 150, nullable = false)
    private String hoTenNhan;

    @Column(name = "SdtNhan", length = 20, nullable = false)
    private String sdtNhan;

    @Column(name = "DiaChiNhan", length = 255, nullable = false)
    private String diaChiNhan;

    @Column(name = "HinhThucTT", length = 10, nullable = false) // COD|VNPAY
    private String hinhThucTT;

    @Column(name = "TrangThai", length = 20, nullable = false) // Tao/Duyet/VanChuyen/HoanTat/Huy
    private String trangThai;

    @Column(name = "TongTien", precision = 18, scale = 2, nullable = false)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(name = "GiamGia", precision = 18, scale = 2, nullable = false)
    private BigDecimal giamGia = BigDecimal.ZERO;

    @Column(name = "PhiShip", precision = 18, scale = 2, nullable = false)
    private BigDecimal phiShip = BigDecimal.ZERO;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}

