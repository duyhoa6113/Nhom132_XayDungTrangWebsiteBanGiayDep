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
@Table(name = "SanPham")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "Ten", nullable = false, length = 200)
    private String ten;

    @Column(name = "MoTa", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SanPhamChiTiet> sanPhamChiTiets;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<YeuThich> yeuThichs;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DanhGia> danhGias;

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

    // ==================== TRANSIENT FIELDS ====================

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

    /**
     * Lấy hình ảnh chính (từ biến thể đầu tiên)
     */
    @Transient
    public String getHinhAnhChinh() {
        if (sanPhamChiTiets != null && !sanPhamChiTiets.isEmpty()) {
            for (SanPhamChiTiet spct : sanPhamChiTiets) {
                if (spct.getHinhAnh() != null && !spct.getHinhAnh().isEmpty()) {
                    return spct.getHinhAnh();
                }
            }
        }
        return "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600";
    }

    /**
     * Lấy danh sách hình ảnh từ các biến thể
     */
    @Transient
    public List<String> getDanhSachHinhAnh() {
        if (sanPhamChiTiets != null) {
            return sanPhamChiTiets.stream()
                    .map(SanPhamChiTiet::getHinhAnh)
                    .filter(img -> img != null && !img.isEmpty())
                    .distinct()
                    .limit(5)
                    .toList();
        }
        return List.of();
    }

    /**
     * Lấy giá thấp nhất
     */
    @Transient
    public BigDecimal getGiaMin() {
        if (sanPhamChiTiets != null && !sanPhamChiTiets.isEmpty()) {
            return sanPhamChiTiets.stream()
                    .filter(spct -> spct.getTrangThai() == 1)
                    .map(SanPhamChiTiet::getGiaBan)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Lấy giá gốc thấp nhất
     */
    @Transient
    public BigDecimal getGiaGocMin() {
        if (sanPhamChiTiets != null && !sanPhamChiTiets.isEmpty()) {
            return sanPhamChiTiets.stream()
                    .filter(spct -> spct.getTrangThai() == 1 && spct.getGiaGoc() != null)
                    .map(SanPhamChiTiet::getGiaGoc)
                    .filter(gia -> gia.compareTo(BigDecimal.ZERO) > 0)
                    .min(BigDecimal::compareTo)
                    .orElse(null);
        }
        return null;
    }

    /**
     * Lấy giá cao nhất
     */
    @Transient
    public BigDecimal getGiaMax() {
        if (sanPhamChiTiets != null && !sanPhamChiTiets.isEmpty()) {
            return sanPhamChiTiets.stream()
                    .filter(spct -> spct.getTrangThai() == 1)
                    .map(SanPhamChiTiet::getGiaBan)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Lấy tổng số lượng tồn kho
     */
    @Transient
    public Integer getSoLuongTonKho() {
        if (sanPhamChiTiets != null && !sanPhamChiTiets.isEmpty()) {
            return sanPhamChiTiets.stream()
                    .filter(spct -> spct.getTrangThai() == 1)
                    .mapToInt(SanPhamChiTiet::getSoLuongTon)
                    .sum();
        }
        return 0;
    }

    /**
     * Tính tỷ lệ giảm giá
     */
    @Transient
    public Integer getTyLeGiamGia() {
        BigDecimal giaGoc = getGiaGocMin();
        BigDecimal giaBan = getGiaMin();

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
     * Format giá thấp nhất
     */
    @Transient
    public String getGiaMinFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(getGiaMin());
    }

    /**
     * Format giá gốc thấp nhất
     */
    @Transient
    public String getGiaGocMinFormatted() {
        BigDecimal giaGoc = getGiaGocMin();
        if (giaGoc != null) {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            return formatter.format(giaGoc);
        }
        return null;
    }

    /**
     * Format giá cao nhất
     */
    @Transient
    public String getGiaMaxFormatted() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(getGiaMax());
    }

    /**
     * Lấy khoảng giá
     */
    @Transient
    public String getKhoangGia() {
        BigDecimal min = getGiaMin();
        BigDecimal max = getGiaMax();

        if (min.compareTo(max) == 0) {
            return getGiaMinFormatted() + "₫";
        } else {
            return getGiaMinFormatted() + "₫ - " + getGiaMaxFormatted() + "₫";
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
     * Kiểm tra còn hàng không
     */
    @Transient
    public boolean isConHang() {
        return getSoLuongTonKho() > 0;
    }

    /**
     * Kiểm tra sắp hết hàng
     */
    @Transient
    public boolean isSapHetHang() {
        Integer tonKho = getSoLuongTonKho();
        return tonKho > 0 && tonKho <= 10;
    }

    /**
     * Lấy trạng thái tồn kho
     */
    @Transient
    public String getTrangThaiTonKho() {
        Integer tonKho = getSoLuongTonKho();
        if (tonKho == 0) {
            return "Hết hàng";
        } else if (tonKho <= 10) {
            return "Sắp hết";
        } else {
            return "Còn hàng";
        }
    }

    /**
     * Lấy màu badge tồn kho
     */
    @Transient
    public String getColorBadgeTonKho() {
        Integer tonKho = getSoLuongTonKho();
        if (tonKho == 0) {
            return "danger";
        } else if (tonKho <= 10) {
            return "warning";
        } else {
            return "success";
        }
    }

    /**
     * Tính điểm đánh giá trung bình
     */
    @Transient
    public Double getDiemDanhGiaTrungBinh() {
        if (danhGias != null && !danhGias.isEmpty()) {
            return danhGias.stream()
                    .filter(dg -> dg.getTrangThai() == 1)
                    .mapToInt(DanhGia::getDiemSao)
                    .average()
                    .orElse(0.0);
        }
        return 0.0;
    }

    /**
     * Đếm số lượng đánh giá
     */
    @Transient
    public long getSoLuongDanhGia() {
        if (danhGias != null) {
            return danhGias.stream()
                    .filter(dg -> dg.getTrangThai() == 1)
                    .count();
        }
        return 0;
    }

    /**
     * Render sao đánh giá
     */
    @Transient
    public String renderStars() {
        Double diem = getDiemDanhGiaTrungBinh();
        StringBuilder stars = new StringBuilder();

        for (int i = 1; i <= 5; i++) {
            if (i <= diem) {
                stars.append("<i class='fas fa-star text-warning'></i>");
            } else if (i - 0.5 <= diem) {
                stars.append("<i class='fas fa-star-half-alt text-warning'></i>");
            } else {
                stars.append("<i class='far fa-star text-warning'></i>");
            }
        }

        return stars.toString();
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
     * Kiểm tra sản phẩm mới (trong vòng 30 ngày)
     */
    @Transient
    public boolean isSanPhamMoi() {
        if (createdAt == null) return false;
        return createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }

    /**
     * Lấy số lượng biến thể
     */
    @Transient
    public int getSoLuongBienThe() {
        return sanPhamChiTiets != null ? sanPhamChiTiets.size() : 0;
    }

    /**
     * Lấy danh sách màu sắc có sẵn
     */
    @Transient
    public List<MauSac> getDanhSachMauSac() {
        if (sanPhamChiTiets != null) {
            return sanPhamChiTiets.stream()
                    .filter(spct -> spct.getTrangThai() == 1)
                    .map(SanPhamChiTiet::getMauSac)
                    .distinct()
                    .toList();
        }
        return List.of();
    }

    /**
     * Lấy danh sách kích thước có sẵn
     */
    @Transient
    public List<KichThuoc> getDanhSachKichThuoc() {
        if (sanPhamChiTiets != null) {
            return sanPhamChiTiets.stream()
                    .filter(spct -> spct.getTrangThai() == 1)
                    .map(SanPhamChiTiet::getKichThuoc)
                    .distinct()
                    .toList();
        }
        return List.of();
    }

    /**
     * Kiểm tra có mô tả không
     */
    @Transient
    public boolean isCoMoTa() {
        return moTa != null && !moTa.trim().isEmpty();
    }

    /**
     * Lấy mô tả ngắn gọn
     */
    @Transient
    public String getMoTaNgan(int maxLength) {
        if (!isCoMoTa()) return "";
        if (moTa.length() <= maxLength) return moTa;
        return moTa.substring(0, maxLength) + "...";
    }
}
