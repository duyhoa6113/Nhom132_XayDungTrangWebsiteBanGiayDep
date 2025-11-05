package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "LichSuTrangThai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichSuTrangThai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LichSuId")
    private Integer lichSuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HoaDonId", nullable = false)
    private HoaDon hoaDon;

    @Column(name = "TrangThaiCu", length = 50)
    private String trangThaiCu;

    @Column(name = "TrangThaiMoi", nullable = false, length = 50)
    private String trangThaiMoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NhanVienId")
    private NhanVien nhanVien;

    @Column(name = "GhiChu", columnDefinition = "NVARCHAR(MAX)")
    private String ghiChu;

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
     * Lấy tên nhân viên
     */
    @Transient
    public String getTenNhanVien() {
        return nhanVien != null ? nhanVien.getHoTen() : "Hệ thống";
    }

    /**
     * Format ngày tạo
     */
    @Transient
    public String getCreatedAtFormatted() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return createdAt.format(formatter);
        }
        return "";
    }

    /**
     * Lấy tên trạng thái cũ
     */
    @Transient
    public String getTenTrangThaiCu() {
        return getTenTrangThai(trangThaiCu);
    }

    /**
     * Lấy tên trạng thái mới
     */
    @Transient
    public String getTenTrangThaiMoi() {
        return getTenTrangThai(trangThaiMoi);
    }

    /**
     * Helper method để convert trạng thái sang tiếng Việt
     */
    private String getTenTrangThai(String trangThai) {
        if (trangThai == null) return "";
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
}
