package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "MauSac")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MauSacId")
    private Integer mauSacId;

    @Column(name = "Ten", nullable = false, unique = true, length = 50)
    private String ten;

    @Column(name = "MaHex", length = 7)
    private String maHex;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "mauSac", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SanPhamChiTiet> sanPhamChiTiets;

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
     * Lấy tên trạng thái
     */
    @Transient
    public String getTenTrangThai() {
        return trangThai == 1 ? "Hoạt động" : "Ngừng hoạt động";
    }

    /**
     * Kiểm tra đang hoạt động
     */
    @Transient
    public boolean isDangHoatDong() {
        return trangThai == 1;
    }

    /**
     * Kiểm tra có mã màu hex
     */
    @Transient
    public boolean isCoMaHex() {
        return maHex != null && !maHex.trim().isEmpty();
    }

    /**
     * Lấy mã hex hoặc mặc định
     */
    @Transient
    public String getMaHexOrDefault() {
        return isCoMaHex() ? maHex : "#CCCCCC";
    }

    /**
     * Render color box HTML
     */
    @Transient
    public String renderColorBox() {
        return String.format(
                "<div style='width: 30px; height: 30px; background-color: %s; border: 1px solid #ddd; border-radius: 4px;'></div>",
                getMaHexOrDefault()
        );
    }

    /**
     * Đếm số biến thể sản phẩm
     */
    @Transient
    public int getSoLuongBienThe() {
        return sanPhamChiTiets != null ? sanPhamChiTiets.size() : 0;
    }

    /**
     * Lấy màu badge trạng thái
     */
    @Transient
    public String getColorBadge() {
        return trangThai == 1 ? "success" : "secondary";
    }

    /**
     * Kiểm tra màu sáng hay tối (để chọn màu chữ phù hợp)
     */
    @Transient
    public boolean isLightColor() {
        if (!isCoMaHex()) return true;

        try {
            String hex = maHex.replace("#", "");
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            // Tính độ sáng (luminance)
            double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
            return luminance > 0.5;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Lấy màu chữ phù hợp với nền
     */
    @Transient
    public String getTextColor() {
        return isLightColor() ? "#000000" : "#FFFFFF";
    }
}
