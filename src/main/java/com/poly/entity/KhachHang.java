package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "KhachHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KhachHangId")
    private Integer khachHangId;

    @Column(name = "HoTen", nullable = false, length = 150)
    private String hoTen;

    @Column(name = "Email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "Sdt", length = 20)
    private String sdt;

    @Column(name = "MatKhauHash", nullable = false, length = 255)
    private String matKhauHash;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "GioiTinh", length = 10)
    private String gioiTinh;

    @Column(name = "Avatar", length = 500)
    private String avatar;

    @Column(name = "ResetToken", length = 255)
    private String resetToken;

    @Column(name = "ResetTokenExpiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DiaChi> diaChis = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HoaDon> hoaDons = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<GioHang> gioHangs = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DanhGia> danhGias = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ThongBao> thongBaos = new java.util.ArrayList<>();

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

    // Các transient methods giữ nguyên...
    @Transient
    public String getNgaySinhFormatted() {
        if (ngaySinh != null) {
            java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return ngaySinh.format(formatter);
        }
        return "";
    }

    @Transient
    public String getAvatarOrDefault() {
        if (avatar != null && !avatar.isEmpty()) {
            return avatar;
        }
        return "/images/default-avatar.png";
    }

    @Transient
    public boolean isResetTokenValid() {
        return resetToken != null && resetTokenExpiry != null &&
                LocalDateTime.now().isBefore(resetTokenExpiry);
    }
}
