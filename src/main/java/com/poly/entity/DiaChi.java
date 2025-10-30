package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity cho bảng DiaChi
 * Lưu thông tin địa chỉ giao hàng của khách hàng
 */
@Entity
@Table(name = "DiaChi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "DiaChi", nullable = false, length = 255)
    private String diaChi;

    @Column(name = "PhuongXa", length = 150)
    private String phuongXa;

    @Column(name = "QuanHuyen", length = 150)
    private String quanHuyen;

    @Column(name = "TinhTP", length = 150)
    private String tinhTP;

    @Column(name = "MacDinh", nullable = false)
    private Boolean macDinh = false;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (macDinh == null) {
            macDinh = false;
        }
    }

    /**
     * Lấy địa chỉ đầy đủ dạng chuỗi
     */
    public String getDiaChiDayDu() {
        StringBuilder sb = new StringBuilder(diaChi);
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
}