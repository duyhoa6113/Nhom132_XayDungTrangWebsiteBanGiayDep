package com.poly.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Entity cho bảng SanPham
 *
 * @author Nhóm 132
 */
@Entity
@Table(name = "SanPham")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SanPhamId")
    private Integer sanPhamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DanhMucId", nullable = false)
    private DanhMuc danhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ThuongHieuId")
    private ThuongHieu thuongHieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ChatLieuId")
    private ChatLieu chatLieu;

    @OneToMany(mappedBy = "sanPham", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SanPhamChiTiet> variants;

    @Column(name = "Ten", nullable = false, length = 200)
    private String ten;

    @Column(name = "MoTa", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "TrangThai", nullable = false)
    private int trangThai = 1;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "SoLuongDaBan")
    private Integer soLuongDaBan;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Lấy hình ảnh chính của sản phẩm (từ variant đầu tiên)
     */
    @Transient
    public String getHinhAnhChinh() {
        if (variants != null && !variants.isEmpty()) {
            for (SanPhamChiTiet variant : variants) {
                if (variant.getHinhAnh() != null && !variant.getHinhAnh().isEmpty()) {
                    return variant.getHinhAnh();
                }
            }
        }
        return null;
    }

    /**
     * Lấy giá thấp nhất của sản phẩm
     * ✅ FIX: Dùng BigDecimal thay vì mapToDouble
     */
    @Transient
    public Double getGiaMin() {
        if (variants != null && !variants.isEmpty()) {
            Optional<BigDecimal> min = variants.stream()
                    .filter(v -> v.getTrangThai() == 1)
                    .map(SanPhamChiTiet::getGiaBan)
                    .filter(price -> price != null)
                    .min(Comparator.naturalOrder());

            return min.map(BigDecimal::doubleValue).orElse(0.0);
        }
        return 0.0;
    }

    /**
     * Lấy giá gốc thấp nhất (trước khi giảm)
     * ✅ FIX: Dùng BigDecimal thay vì mapToDouble
     */
    @Transient
    public Double getGiaGocMin() {
        if (variants != null && !variants.isEmpty()) {
            Optional<BigDecimal> min = variants.stream()
                    .filter(v -> v.getTrangThai() == 1 && v.getGiaGoc() != null)
                    .map(SanPhamChiTiet::getGiaGoc)
                    .min(Comparator.naturalOrder());

            return min.map(BigDecimal::doubleValue).orElse(null);
        }
        return null;
    }

    /**
     * Format giá hiển thị (ví dụ: 1.500.000)
     */
    @Transient
    public String getGiaMinFormatted() {
        Double giaMin = getGiaMin();
        if (giaMin == null || giaMin == 0.0) return "0";
        return String.format("%,.0f", giaMin).replace(',', '.');
    }

    /**
     * Format giá gốc hiển thị
     */
    @Transient
    public String getGiaGocMinFormatted() {
        Double giaGoc = getGiaGocMin();
        if (giaGoc != null) {
            return String.format("%,.0f", giaGoc).replace(',', '.');
        }
        return null;
    }

    /**
     * Tính tỷ lệ giảm giá (%)
     */
    @Transient
    public Integer getTyLeGiamGia() {
        Double giaGoc = getGiaGocMin();
        Double giaBan = getGiaMin();
        if (giaGoc != null && giaGoc > 0 && giaBan < giaGoc) {
            return (int) Math.round(((giaGoc - giaBan) / giaGoc) * 100);
        }
        return 0;
    }

    /**
     * Lấy tên thương hiệu
     */
    @Transient
    public String getThuongHieuTen() {
        return thuongHieu != null ? thuongHieu.getTen() : "";
    }

    /**
     * Lấy tên danh mục
     */
    @Transient
    public String getDanhMucTen() {
        return danhMuc != null ? danhMuc.getTen() : "";
    }

    /**
     * Lấy tên chất liệu
     */
    @Transient
    public String getChatLieuTen() {
        return chatLieu != null ? chatLieu.getTen() : "";
    }

    public Integer getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(Integer sanPhamId) {
        this.sanPhamId = sanPhamId;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }

    public ThuongHieu getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(ThuongHieu thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public ChatLieu getChatLieu() {
        return chatLieu;
    }

    public void setChatLieu(ChatLieu chatLieu) {
        this.chatLieu = chatLieu;
    }

    public List<SanPhamChiTiet> getVariants() {
        return variants;
    }

    public void setVariants(List<SanPhamChiTiet> variants) {
        this.variants = variants;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getSoLuongDaBan() {
        return soLuongDaBan;
    }

    public void setSoLuongDaBan(Integer soLuongDaBan) {
        this.soLuongDaBan = soLuongDaBan;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham sanPham = (SanPham) o;
        return sanPhamId != null && sanPhamId.equals(sanPham.sanPhamId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SanPham{" +
                "sanPhamId=" + sanPhamId +
                ", ten='" + ten + '\'' +
                ", trangThai=" + trangThai +
                '}';
    }

}