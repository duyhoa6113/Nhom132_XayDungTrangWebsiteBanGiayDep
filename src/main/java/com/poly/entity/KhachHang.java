package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "GioiTinh", length = 1)
    private String gioiTinh;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai;

    // Trường cho reset password
    @Column(name = "ResetToken", length = 255)
    private String resetToken;

    @Column(name = "ResetTokenExpiry")
    private LocalDateTime resetTokenExpiry;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = 1;
        }
    }

    /**
     * Kiểm tra reset token đã hết hạn chưa
     */
    public boolean isResetTokenExpired() {
        if (resetTokenExpiry == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(resetTokenExpiry);
    }

    /**
     * Xóa reset token
     */
    public void clearResetToken() {
        this.resetToken = null;
        this.resetTokenExpiry = null;
    }
}