package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "ThuongHieu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThuongHieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ThuongHieuId")
    private Integer thuongHieuId;

    @Column(name = "Ten", nullable = false, unique = true, length = 150)
    private String ten;

    @Column(name = "MoTa", length = 1000)
    private String moTa;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "thuongHieu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SanPham> sanPhams;

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
     * Đếm số sản phẩm
     */
    @Transient
    public int getSoLuongSanPham() {
        return sanPhams != null ? sanPhams.size() : 0;
    }

    /**
     * Lấy màu badge trạng thái
     */
    @Transient
    public String getColorBadge() {
        return trangThai == 1 ? "success" : "secondary";
    }
}
