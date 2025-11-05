package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "NhanVien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NhanVienId")
    private Integer nhanVienId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VaiTroId", nullable = false)
    private VaiTro vaiTro;

    @Column(name = "HoTen", nullable = false, length = 150)
    private String hoTen;

    @Column(name = "Email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "MatKhauHash", nullable = false, length = 255)
    private String matKhauHash;

    @Column(name = "Sdt", length = 20)
    private String sdt;

    @Column(name = "DiaChi", length = 500)
    private String diaChi;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "GioiTinh", length = 10)
    private String gioiTinh;

    @Column(name = "ChucVu", length = 50)
    private String chucVu;

    @Column(name = "Avatar", length = 500)
    private String avatar;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "LanDangNhapCuoi")
    private LocalDateTime lanDangNhapCuoi;

    @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;

    @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LichSuTrangThai> lichSuTrangThais;

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

    @Transient
    public String getTenVaiTro() {
        return vaiTro != null ? vaiTro.getTenVaiTro() : "";
    }
}
