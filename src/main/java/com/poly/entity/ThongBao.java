package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "ThongBao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ThongBaoId")
    private Integer thongBaoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KhachHangId")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NhanVienId")
    private NhanVien nhanVien;

    @Column(name = "TieuDe", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "NoiDung", nullable = false, length = 1000)
    private String noiDung;

    @Column(name = "Loai", nullable = false, length = 50)
    private String loai;

    @Column(name = "LienKet", length = 500)
    private String lienKet;

    @Column(name = "DaDoc", nullable = false)
    private Boolean daDoc;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (daDoc == null) {
            daDoc = false;
        }
    }

    // ==================== TRANSIENT FIELDS ====================

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
     * Lấy thời gian tương đối
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
        } else {
            return getCreatedAtFormatted();
        }
    }

    /**
     * Lấy icon theo loại thông báo
     */
    @Transient
    public String getIcon() {
        switch (loai) {
            case "DonHang": return "fa-shopping-cart";
            case "KhuyenMai": return "fa-gift";
            case "ThanhToan": return "fa-credit-card";
            case "HeThong": return "fa-bell";
            default: return "fa-info-circle";
        }
    }

    /**
     * Lấy màu theo loại thông báo
     */
    @Transient
    public String getColorClass() {
        switch (loai) {
            case "DonHang": return "primary";
            case "KhuyenMai": return "success";
            case "ThanhToan": return "warning";
            case "HeThong": return "info";
            default: return "secondary";
        }
    }
}
