package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "DanhGia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DanhGiaId")
    private Integer danhGiaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SanPhamId", nullable = false)
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KhachHangId", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HoaDonId")
    private HoaDon hoaDon;

    @Column(name = "DiemSao", nullable = false)
    private Byte diemSao;

    @Column(name = "NoiDung", length = 1000)
    private String noiDung;

    @Column(name = "HinhAnh", columnDefinition = "NVARCHAR(MAX)")
    private String hinhAnh;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = 1;
        }
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
     * Lấy avatar khách hàng
     */
    @Transient
    public String getAvatarKhachHang() {
        return khachHang != null ? khachHang.getAvatar() : null;
    }

    /**
     * Lấy tên sản phẩm
     */
    @Transient
    public String getTenSanPham() {
        return sanPham != null ? sanPham.getTen() : "";
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
     * Format ngày tạo ngắn gọn
     */
    @Transient
    public String getCreatedAtShort() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return createdAt.format(formatter);
        }
        return "";
    }

    /**
     * Lấy thời gian tương đối (vd: 2 giờ trước)
     */
    @Transient
    public String getTimeAgo() {
        if (createdAt == null) return "";

        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(createdAt, now).getSeconds();

        if (seconds < 60) {
            return "Vừa xong";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " phút trước";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " giờ trước";
        } else if (seconds < 2592000) {
            long days = seconds / 86400;
            return days + " ngày trước";
        } else if (seconds < 31536000) {
            long months = seconds / 2592000;
            return months + " tháng trước";
        } else {
            long years = seconds / 31536000;
            return years + " năm trước";
        }
    }

    /**
     * Kiểm tra có hình ảnh không
     */
    @Transient
    public boolean isCoHinhAnh() {
        return hinhAnh != null && !hinhAnh.trim().isEmpty();
    }

    /**
     * Lấy danh sách hình ảnh (nếu có nhiều hình)
     */
    @Transient
    public String[] getDanhSachHinhAnh() {
        if (hinhAnh != null && !hinhAnh.trim().isEmpty()) {
            return hinhAnh.split(",");
        }
        return new String[0];
    }

    /**
     * Lấy số lượng hình ảnh
     */
    @Transient
    public int getSoLuongHinhAnh() {
        return getDanhSachHinhAnh().length;
    }

    /**
     * Render sao HTML
     */
    @Transient
    public String renderStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= diemSao) {
                stars.append("<i class='fas fa-star text-warning'></i>");
            } else {
                stars.append("<i class='far fa-star text-warning'></i>");
            }
        }
        return stars.toString();
    }

    /**
     * Kiểm tra đánh giá tích cực
     */
    @Transient
    public boolean isDanhGiaTot() {
        return diemSao != null && diemSao >= 4;
    }

    /**
     * Kiểm tra đánh giá trung bình
     */
    @Transient
    public boolean isDanhGiaTrungBinh() {
        return diemSao != null && diemSao == 3;
    }

    /**
     * Kiểm tra đánh giá kém
     */
    @Transient
    public boolean isDanhGiaKem() {
        return diemSao != null && diemSao <= 2;
    }

    /**
     * Lấy màu sắc theo đánh giá
     */
    @Transient
    public String getColorClass() {
        if (isDanhGiaTot()) return "success";
        if (isDanhGiaTrungBinh()) return "warning";
        if (isDanhGiaKem()) return "danger";
        return "secondary";
    }
}
