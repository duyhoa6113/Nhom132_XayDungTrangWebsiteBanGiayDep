package com.poly.entity;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import jakarta.persistence.*;
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

    @Column(name = "MaHoaDon", nullable = false, unique = true, length = 50)
    private String maHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KhachHangId", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NhanVienId")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KhuyenMaiId")
    private KhuyenMai khuyenMai;

    @Column(name = "HoTenNhan", nullable = false, length = 150)
    private String hoTenNhan;

    @Column(name = "SdtNhan", nullable = false, length = 20)
    private String sdtNhan;

    @Column(name = "DiaChiNhan", nullable = false, length = 500)
    private String diaChiNhan;

    @Column(name = "PhuongXa", length = 150)
    private String phuongXa;

    @Column(name = "QuanHuyen", length = 150)
    private String quanHuyen;

    @Column(name = "TinhTP", length = 150)
    private String tinhTP;

    @Column(name = "PhuongThucThanhToan", nullable = false, length = 50)
    private String phuongThucThanhToan;

    @Column(name = "TrangThai", nullable = false, length = 50)
    private String trangThai;

    @Column(name = "TongTien", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "GiamGia", nullable = false, precision = 18, scale = 2)
    private BigDecimal giamGia;

    @Column(name = "PhiVanChuyen", nullable = false, precision = 18, scale = 2)
    private BigDecimal phiVanChuyen;

    @Column(name = "TongThanhToan", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongThanhToan;

    @Column(name = "GhiChu", columnDefinition = "NVARCHAR(MAX)")
    private String ghiChu;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDonChiTiet> hoaDonChiTiets;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LichSuTrangThai> lichSuTrangThais;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DanhGia> danhGias;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = "ChoXuLy";
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

    // ==================== TRANSIENT FIELDS ====================

    /**
     * Lấy tên khách hàng
     */
    @Transient
    public String getTenKhachHang() {
        return khachHang != null ? khachHang.getHoTen() : "";
    }

    /**
     * Lấy tên nhân viên
     */
    @Transient
    public String getTenNhanVien() {
        return nhanVien != null ? nhanVien.getHoTen() : "";
    }

    /**
     * Lấy địa chỉ đầy đủ
     */
    @Transient
    public String getDiaChiDayDu() {
        StringBuilder sb = new StringBuilder();
        sb.append(diaChiNhan);
        if (phuongXa != null && !phuongXa.isEmpty()) {
            sb.append(", ").append(phuongXa);
        }
        if (quanHuyen != null && !quanHuyen.isEmpty()) {
            sb.append(", ").append(quanHuyen);
        }
        if (tinhTP != null && !tinhTP.isEmpty()) {
            sb.append(", ").append(tinhTP);
        }
        return sb.toString();
    }

    /**
     * Format tổng tiền
     */
    @Transient
    public String getTongTienFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(tongTien);
    }

    /**
     * Format giảm giá
     */
    @Transient
    public String getGiamGiaFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(giamGia);
    }

    /**
     * Format phí vận chuyển
     */
    @Transient
    public String getPhiVanChuyenFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(phiVanChuyen);
    }

    /**
     * Format tổng thanh toán
     */
    @Transient
    public String getTongThanhToanFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(tongThanhToan);
    }

    /**
     * Format ngày tạo
     */
    @Transient
    public String getCreatedAtFormatted() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return createdAt.format(formatter);
        }
        return "";
    }

    /**
     * Lấy tên trạng thái tiếng Việt
     */
    @Transient
    public String getTenTrangThai() {
        switch (trangThai) {
            case "ChoXuLy": return "Chờ xử lý";
            case "DaXacNhan": return "Đã xác nhận";
            case "DangChuanBi": return "Đang chuẩn bị";
            case "DangGiao": return "Đang giao";
            case "DaGiao": return "Đã giao";
            case "HoanThanh": return "Hoàn thành";
            case "DaHuy": return "Đã hủy";
            default: return trangThai;
        }
    }

    /**
     * Lấy màu trạng thái
     */
    @Transient
    public String getColorTrangThai() {
        switch (trangThai) {
            case "ChoXuLy": return "warning";
            case "DaXacNhan": return "info";
            case "DangChuanBi": return "primary";
            case "DangGiao": return "primary";
            case "DaGiao": return "success";
            case "HoanThanh": return "success";
            case "DaHuy": return "danger";
            default: return "secondary";
        }
    }

    /**
     * Kiểm tra có thể hủy không
     */
    @Transient
    public boolean isCoTheHuy() {
        return "ChoXuLy".equals(trangThai) || "DaXacNhan".equals(trangThai);
    }

    /**
     * Kiểm tra có thể đánh giá không
     */
    @Transient
    public boolean isCoDanhGia() {
        return "HoanThanh".equals(trangThai);
    }
}
