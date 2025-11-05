package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "SanPhamChiTiet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VariantId")
    private Integer variantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SanPhamId", nullable = false)
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MauSacId", nullable = false)
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "KichThuocId", nullable = false)
    private KichThuoc kichThuoc;

    @Column(name = "SKU", nullable = false, unique = true, length = 64)
    private String sku;

    @Column(name = "Barcode", unique = true, length = 64)
    private String barcode;

    @Column(name = "GiaBan", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "GiaGoc", precision = 18, scale = 2)
    private BigDecimal giaGoc;

    @Column(name = "SoLuongTon", nullable = false)
    private Integer soLuongTon;

    @Column(name = "HinhAnh", length = 512)
    private String hinhAnh;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GioHang> gioHangs;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDonChiTiet> hoaDonChiTiets;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = 1;
        }
        if (soLuongTon == null) {
            soLuongTon = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ==================== TRANSIENT FIELDS ====================

    /**
     * Lấy tên sản phẩm
     */
    @Transient
    public String getTenSanPham() {
        return sanPham != null ? sanPham.getTen() : "";
    }

    /**
     * Lấy tên màu sắc
     */
    @Transient
    public String getTenMauSac() {
        return mauSac != null ? mauSac.getTen() : "";
    }

    /**
     * Lấy tên kích thước
     */
    @Transient
    public String getTenKichThuoc() {
        return kichThuoc != null ? kichThuoc.getTen() : "";
    }

    /**
     * Lấy mã hex màu sắc
     */
    @Transient
    public String getMaHexMauSac() {
        return mauSac != null ? mauSac.getMaHexOrDefault() : "#CCCCCC";
    }

    /**
     * Lấy tên đầy đủ (Tên SP - Màu - Size)
     */
    @Transient
    public String getTenDayDu() {
        return String.format("%s - %s - Size %s",
                getTenSanPham(), getTenMauSac(), getTenKichThuoc());
    }

    /**
     * Format giá bán
     */
    @Transient
    public String getGiaBanFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(giaBan);
    }

    /**
     * Format giá gốc
     */
    @Transient
    public String getGiaGocFormatted() {
        if (giaGoc != null && giaGoc.compareTo(BigDecimal.ZERO) > 0) {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            return formatter.format(giaGoc);
        }
        return null;
    }

    /**
     * Tính tỷ lệ giảm giá
     */
    @Transient
    public Integer getTyLeGiamGia() {
        if (giaGoc != null && giaGoc.compareTo(BigDecimal.ZERO) > 0
                && giaBan.compareTo(giaGoc) < 0) {
            BigDecimal giam = giaGoc.subtract(giaBan);
            BigDecimal tyLe = giam.divide(giaGoc, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
            return tyLe.intValue();
        }
        return 0;
    }

    /**
     * Tính số tiền giảm
     */
    @Transient
    public BigDecimal getSoTienGiam() {
        if (giaGoc != null && giaGoc.compareTo(BigDecimal.ZERO) > 0
                && giaBan.compareTo(giaGoc) < 0) {
            return giaGoc.subtract(giaBan);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Format số tiền giảm
     */
    @Transient
    public String getSoTienGiamFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(getSoTienGiam());
    }

    /**
     * Kiểm tra còn hàng không
     */
    @Transient
    public boolean isConHang() {
        return soLuongTon != null && soLuongTon > 0 && trangThai == 1;
    }

    /**
     * Kiểm tra sắp hết hàng
     */
    @Transient
    public boolean isSapHetHang() {
        return soLuongTon != null && soLuongTon > 0 && soLuongTon <= 5;
    }

    /**
     * Kiểm tra hết hàng
     */
    @Transient
    public boolean isHetHang() {
        return soLuongTon == null || soLuongTon == 0;
    }

    /**
     * Lấy trạng thái tồn kho
     */
    @Transient
    public String getTrangThaiTonKho() {
        if (isHetHang()) {
            return "Hết hàng";
        } else if (isSapHetHang()) {
            return "Sắp hết";
        } else {
            return "Còn " + soLuongTon;
        }
    }

    /**
     * Lấy màu badge tồn kho
     */
    @Transient
    public String getColorBadgeTonKho() {
        if (isHetHang()) {
            return "danger";
        } else if (isSapHetHang()) {
            return "warning";
        } else {
            return "success";
        }
    }

    /**
     * Kiểm tra có đang giảm giá không
     */
    @Transient
    public boolean isDangGiamGia() {
        return getTyLeGiamGia() > 0;
    }

    /**
     * Lấy hình ảnh hoặc mặc định
     */
    @Transient
    public String getHinhAnhOrDefault() {
        if (hinhAnh != null && !hinhAnh.trim().isEmpty()) {
            return hinhAnh;
        }
        return "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600";
    }

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
     * Format ngày cập nhật
     */
    @Transient
    public String getUpdatedAtFormatted() {
        if (updatedAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return updatedAt.format(formatter);
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
     * Lấy màu badge trạng thái
     */
    @Transient
    public String getColorBadgeTrangThai() {
        return trangThai == 1 ? "success" : "secondary";
    }

    /**
     * Kiểm tra có thể mua không
     */
    @Transient
    public boolean isCoTheMua() {
        return trangThai == 1 && isConHang();
    }

    /**
     * Lấy thông báo trạng thái
     */
    @Transient
    public String getThongBaoTrangThai() {
        if (!isCoTheMua()) {
            if (trangThai == 0) {
                return "Sản phẩm ngừng kinh doanh";
            } else if (isHetHang()) {
                return "Sản phẩm tạm hết hàng";
            }
        } else if (isSapHetHang()) {
            return "Chỉ còn " + soLuongTon + " sản phẩm";
        }
        return "";
    }

    /**
     * Tính giá trị tồn kho
     */
    @Transient
    public BigDecimal getGiaTriTonKho() {
        if (soLuongTon != null && giaBan != null) {
            return giaBan.multiply(new BigDecimal(soLuongTon));
        }
        return BigDecimal.ZERO;
    }

    /**
     * Format giá trị tồn kho
     */
    @Transient
    public String getGiaTriTonKhoFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(getGiaTriTonKho());
    }

    /**
     * Lấy thương hiệu
     */
    @Transient
    public String getThuongHieu() {
        return sanPham != null && sanPham.getThuongHieu() != null ?
                sanPham.getThuongHieu().getTen() : "";
    }

    /**
     * Lấy danh mục
     */
    @Transient
    public String getDanhMuc() {
        return sanPham != null && sanPham.getDanhMuc() != null ?
                sanPham.getDanhMuc().getTen() : "";
    }

    /**
     * Kiểm tra có barcode không
     */
    @Transient
    public boolean isCoBarcode() {
        return barcode != null && !barcode.trim().isEmpty();
    }

    /**
     * Render color box
     */
    @Transient
    public String renderColorBox() {
        return String.format(
                "<div style='width: 25px; height: 25px; background-color: %s; border: 1px solid #ddd; border-radius: 4px; display: inline-block;'></div>",
                getMaHexMauSac()
        );
    }

    /**
     * Lấy label size
     */
    @Transient
    public String getSizeLabel() {
        return "Size " + getTenKichThuoc();
    }
}
