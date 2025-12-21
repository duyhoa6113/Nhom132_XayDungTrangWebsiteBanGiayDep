package com.poly.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "HoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HoaDonId")
    private Integer hoaDonId;

    @Column(name = "MaHoaDon", unique = true, length = 50, nullable = false)
    private String maHoaDon;

    @ManyToOne
    @JoinColumn(name = "KhachHangId", nullable = false)
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "NhanVienId")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "KhuyenMaiId")
    private KhuyenMai khuyenMai;

    @Column(name = "HoTenNhan", length = 150, nullable = false)
    private String hoTenNhan;

    @Column(name = "SdtNhan", length = 20, nullable = false)
    private String sdtNhan;

    @Column(name = "DiaChiNhan", length = 500, nullable = false)
    private String diaChiNhan;

    @Column(name = "PhuongXa", length = 150)
    private String phuongXa;

    @Column(name = "QuanHuyen", length = 150)
    private String quanHuyen;

    @Column(name = "TinhTP", length = 150)
    private String tinhTP;

    @Column(name = "PhuongThucThanhToan", length = 50, nullable = false)
    private String phuongThucThanhToan;

    @Column(name = "TrangThai", length = 50, nullable = false)
    private String trangThai;

    // ✅ THÊM TRƯỜNG MỚI NÀY
    // Lưu ý: Cần chạy script SQL để thêm cột này vào database (xem file add_trangthai_thanhtoan.sql)
    // File script: src/main/resources/static/db/add_trangthai_thanhtoan.sql
    // SAU KHI CHẠY SCRIPT SQL, sửa lại thành: @Column(name = "TrangThaiThanhToan", length = 50, nullable = true)
    // (bỏ insertable = false, updatable = false)
    @Column(name = "TrangThaiThanhToan", length = 50, nullable = true, insertable = false, updatable = false)
    private String trangThaiThanhToan; // UNPAID, PAID, FAILED

    @Column(name = "TongTien", precision = 18, scale = 2, nullable = false)
    private BigDecimal tongTien;

    @Column(name = "GiamGia", precision = 18, scale = 2, nullable = false)
    private BigDecimal giamGia;

    @Column(name = "PhiVanChuyen", precision = 18, scale = 2, nullable = false)
    private BigDecimal phiVanChuyen;

    @Column(name = "TongThanhToan", precision = 18, scale = 2, nullable = false)
    private BigDecimal tongThanhToan;

    @Column(name = "GhiChu", columnDefinition = "NVARCHAR(MAX)")
    private String ghiChu;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HoaDonChiTiet> chiTietList = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (trangThai == null) {
            trangThai = "ChoXuLy";
        }
        // ✅ THÊM DÒNG NÀY
        if (trangThaiThanhToan == null) {
            trangThaiThanhToan = "UNPAID"; // Mặc định chưa thanh toán
        }
        if (giamGia == null) {
            giamGia = BigDecimal.ZERO;
        }
        if (phiVanChuyen == null) {
            phiVanChuyen = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addChiTiet(HoaDonChiTiet chiTiet) {
        chiTietList.add(chiTiet);
        chiTiet.setHoaDon(this);
    }
}