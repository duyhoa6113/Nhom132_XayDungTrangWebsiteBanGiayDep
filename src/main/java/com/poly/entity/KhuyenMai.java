package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "KhuyenMai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KhuyenMaiId")
    private Integer khuyenMaiId;

    @Column(name = "Ma", nullable = false, unique = true, length = 50)
    private String ma;

    @Column(name = "Ten", nullable = false, length = 200)
    private String ten;

    @Column(name = "MoTa", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "Loai", nullable = false, length = 20)
    private String loai; // 'percent' hoặc 'fixed'

    @Column(name = "GiaTri", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTri;

    @Column(name = "GiamToiDa", precision = 18, scale = 2)
    private BigDecimal giamToiDa;

    @Column(name = "DieuKienApDung", precision = 18, scale = 2)
    private BigDecimal dieuKienApDung;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "NgayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "khuyenMai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = 1;
        }
        if (soLuong == null) {
            soLuong = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Kiểm tra khuyến mãi còn hiệu lực
     */
    @Transient
    public boolean isConHieuLuc() {
        LocalDate now = LocalDate.now();
        return trangThai == 1 && soLuong > 0 &&
                !now.isBefore(ngayBatDau) && !now.isAfter(ngayKetThuc);
    }

    /**
     * Tính số tiền giảm
     */
    @Transient
    public BigDecimal tinhSoTienGiam(BigDecimal tongTien) {
        if (!isConHieuLuc()) {
            return BigDecimal.ZERO;
        }

        if (dieuKienApDung != null && tongTien.compareTo(dieuKienApDung) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal soTienGiam;
        if ("percent".equals(loai)) {
            soTienGiam = tongTien.multiply(giaTri).divide(new BigDecimal("100"));
            if (giamToiDa != null && soTienGiam.compareTo(giamToiDa) > 0) {
                soTienGiam = giamToiDa;
            }
        } else {
            soTienGiam = giaTri;
        }

        return soTienGiam;
    }

    /**
     * Format giá trị
     */
    @Transient
    public String getGiaTriFormatted() {
        if ("percent".equals(loai)) {
            return giaTri.intValue() + "%";
        } else {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            return formatter.format(giaTri) + "₫";
        }
    }
}
