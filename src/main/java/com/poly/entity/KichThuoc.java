package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "KichThuoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KichThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KichThuocId")
    private Integer kichThuocId;

    @Column(name = "Ten", nullable = false, unique = true, length = 50)
    private String ten;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "kichThuoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
     * Parse size number (nếu là số)
     */
    @Transient
    public Integer getSizeNumber() {
        try {
            return Integer.parseInt(ten);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Kiểm tra có phải size số không
     */
    @Transient
    public boolean isNumericSize() {
        return getSizeNumber() != null;
    }

    /**
     * Lấy size display (với đơn vị nếu cần)
     */
    @Transient
    public String getSizeDisplay() {
        if (isNumericSize()) {
            return "Size " + ten;
        }
        return ten;
    }
}
