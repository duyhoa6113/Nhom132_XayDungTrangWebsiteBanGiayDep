package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "DiaChi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaChi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DiaChiId")
    private Integer diaChiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KhachHangId", nullable = false)
    private KhachHang khachHang;

    @Column(name = "HoTenNhan", nullable = false, length = 150)
    private String hoTenNhan;

    @Column(name = "SdtNhan", nullable = false, length = 20)
    private String sdtNhan;

    @Column(name = "DiaChi", nullable = false, length = 500)
    private String diaChi;

    @Column(name = "PhuongXa", length = 150)
    private String phuongXa;

    @Column(name = "QuanHuyen", length = 150)
    private String quanHuyen;

    @Column(name = "TinhTP", length = 150)
    private String tinhTP;

    /**
     * MacDinh: BIT trong SQL Server tương thích với Boolean trong JPA
     * 0 = false, 1 = true
     */
    @Column(name = "MacDinh", nullable = false)
    private Boolean macDinh;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (macDinh == null) {
            macDinh = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Lấy địa chỉ đầy đủ
     */
    @Transient
    public String getDiaChiDayDu() {
        StringBuilder sb = new StringBuilder();
        sb.append(diaChi);
        if (phuongXa != null && !phuongXa.isEmpty()) {
            sb.append(", ").append(phuongXa);
        }
        if (quanHuyen != null && !quanHuyen.isEmpty()) {
            sb.append(", ").append(quanHuyen);
        }
        if (tinhTP != null && !tinhTP.isEmpty()) {
            sb.append(", ").append(tinhTP);
        }
        return sb.toString();
    }

    /**
     * Kiểm tra có phải địa chỉ mặc định không
     */
    @Transient
    public boolean isDiaChinMacDinh() {
        return macDinh != null && macDinh;
    }
}