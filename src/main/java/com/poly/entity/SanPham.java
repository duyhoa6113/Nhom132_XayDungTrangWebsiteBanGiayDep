package com.poly.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity cho bảng SanPham
 * Mapping với database nhom132_shoponline
 */
@Entity
@Table(name = "SanPham")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"variants"}) // Tránh vòng lặp toString
@EqualsAndHashCode(exclude = {"variants"})
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SanPhamId")
    private Integer sanPhamId;

    @Column(name = "Ten", nullable = false, length = 200)
    private String ten;

    @Column(name = "MoTa", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "TrangThai", nullable = false)
    private Integer trangThai = 1;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // ========== RELATIONSHIPS ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DanhMucId", nullable = false)
    private DanhMuc danhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ThuongHieuId")
    private ThuongHieu thuongHieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ChatLieuId")
    private ChatLieu chatLieu;

    @OneToMany(mappedBy = "sanPham", fetch = FetchType.LAZY)
    private List<SanPhamChiTiet> variants;

    // ========== LIFECYCLE CALLBACKS ==========

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}