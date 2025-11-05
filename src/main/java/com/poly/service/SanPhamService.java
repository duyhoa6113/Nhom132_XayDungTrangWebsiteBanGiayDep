package com.poly.service;

import com.poly.entity.SanPham;
import com.poly.entity.SanPhamChiTiet;
import com.poly.repository.SanPhamRepository;
import com.poly.repository.SanPhamChiTietRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    /**
     * Lấy tất cả sản phẩm
     */
    @Transactional(readOnly = true)
    public List<SanPham> layTatCaSanPham() {
        try {
            return sanPhamRepository.findAll();
        } catch (Exception e) {
            log.error("Lỗi khi lấy tất cả sản phẩm: ", e);
            return List.of();
        }
    }

    /**
     * Lấy tất cả sản phẩm có phân trang
     */
    @Transactional(readOnly = true)
    public Page<SanPham> layTatCaSanPham(Pageable pageable) {
        try {
            return sanPhamRepository.findAllWithDetails(pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm phân trang: ", e);
            return Page.empty();
        }
    }

    /**
     * Lấy sản phẩm đang hoạt động
     */
    @Transactional(readOnly = true)
    public Page<SanPham> laySanPhamHoatDong(Pageable pageable) {
        try {
            return sanPhamRepository.findByTrangThai((byte) 1, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm hoạt động: ", e);
            return Page.empty();
        }
    }

    /**
     * Lấy sản phẩm theo ID
     */
    @Transactional(readOnly = true)
    public SanPham laySanPhamTheoId(Integer id) {
        try {
            return sanPhamRepository.findByIdWithDetails(id).orElse(null);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm theo ID {}: ", id, e);
            return null;
        }
    }

    /**
     * Lấy sản phẩm theo danh mục
     */
    @Transactional(readOnly = true)
    public Page<SanPham> laySanPhamTheoDanhMuc(Integer danhMucId, Pageable pageable) {
        try {
            return sanPhamRepository.findByDanhMucDanhMucId(danhMucId, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm theo danh mục: ", e);
            return Page.empty();
        }
    }

    /**
     * Lấy sản phẩm theo thương hiệu
     */
    @Transactional(readOnly = true)
    public Page<SanPham> laySanPhamTheoThuongHieu(Integer thuongHieuId, Pageable pageable) {
        try {
            return sanPhamRepository.findByThuongHieuThuongHieuId(thuongHieuId, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm theo thương hiệu: ", e);
            return Page.empty();
        }
    }

    /**
     * Tìm kiếm sản phẩm
     */
    @Transactional(readOnly = true)
    public Page<SanPham> timKiemSanPham(String keyword, Pageable pageable) {
        try {
            return sanPhamRepository.searchProducts(keyword, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm sản phẩm: ", e);
            return Page.empty();
        }
    }

    /**
     * Lấy sản phẩm theo khoảng giá
     */
    @Transactional(readOnly = true)
    public Page<SanPham> laySanPhamTheoKhoangGia(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        try {
            return sanPhamRepository.findByPriceRange(minPrice, maxPrice, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm theo khoảng giá: ", e);
            return Page.empty();
        }
    }

    /**
     * Lấy sản phẩm mới nhất
     */
    @Transactional(readOnly = true)
    public List<SanPham> laySanPhamMoiNhat(int limit) {
        try {
            return sanPhamRepository.findLatestProducts(limit);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm mới nhất: ", e);
            return List.of();
        }
    }

    /**
     * Lấy sản phẩm bán chạy
     */
    @Transactional(readOnly = true)
    public List<SanPham> laySanPhamBanChay(int limit) {
        try {
            return sanPhamRepository.findBestSellingProducts(limit);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm bán chạy: ", e);
            return List.of();
        }
    }

    /**
     * Lấy sản phẩm liên quan
     */
    @Transactional(readOnly = true)
    public List<SanPham> laySanPhamLienQuan(Integer danhMucId, Integer excludeId, int limit) {
        try {
            return sanPhamRepository.findRelatedProducts(danhMucId, excludeId, limit);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm liên quan: ", e);
            return List.of();
        }
    }

    /**
     * Lấy sản phẩm đang giảm giá
     */
    @Transactional(readOnly = true)
    public Page<SanPham> laySanPhamGiamGia(Pageable pageable) {
        try {
            return sanPhamRepository.findDiscountedProducts(pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm giảm giá: ", e);
            return Page.empty();
        }
    }

    /**
     * Lấy biến thể theo sản phẩm
     */
    @Transactional(readOnly = true)
    public List<SanPhamChiTiet> layBienTheTheoSanPham(Integer sanPhamId) {
        try {
            return sanPhamChiTietRepository.findBySanPhamSanPhamId(sanPhamId);
        } catch (Exception e) {
            log.error("Lỗi khi lấy biến thể theo sản phẩm: ", e);
            return List.of();
        }
    }

    /**
     * Lấy biến thể theo ID
     */
    @Transactional(readOnly = true)
    public SanPhamChiTiet layBienTheTheoId(Integer variantId) {
        try {
            return sanPhamChiTietRepository.findById(variantId).orElse(null);
        } catch (Exception e) {
            log.error("Lỗi khi lấy biến thể theo ID: ", e);
            return null;
        }
    }

    /**
     * Lấy biến thể theo màu và size
     */
    @Transactional(readOnly = true)
    public SanPhamChiTiet layBienTheTheoMauVaSize(Integer sanPhamId, Integer mauSacId, Integer kichThuocId) {
        try {
            return sanPhamChiTietRepository.findBySanPhamSanPhamIdAndMauSacMauSacIdAndKichThuocKichThuocId(
                    sanPhamId, mauSacId, kichThuocId).orElse(null);
        } catch (Exception e) {
            log.error("Lỗi khi lấy biến thể theo màu và size: ", e);
            return null;
        }
    }

    /**
     * Thêm sản phẩm mới
     */
    @Transactional
    public SanPham themSanPham(SanPham sanPham) {
        try {
            sanPham.setTrangThai((byte) 1);
            sanPham.setCreatedAt(LocalDateTime.now());

            SanPham saved = sanPhamRepository.save(sanPham);
            log.info("Đã thêm sản phẩm mới: {}", saved.getTen());
            return saved;
        } catch (Exception e) {
            log.error("Lỗi khi thêm sản phẩm: ", e);
            return null;
        }
    }

    /**
     * Cập nhật sản phẩm
     */
    @Transactional
    public SanPham capNhatSanPham(Integer id, SanPham sanPham) {
        try {
            SanPham existing = sanPhamRepository.findById(id).orElse(null);
            if (existing == null) {
                log.warn("Không tìm thấy sản phẩm với ID: {}", id);
                return null;
            }

            existing.setTen(sanPham.getTen());
            existing.setMoTa(sanPham.getMoTa());
            existing.setDanhMuc(sanPham.getDanhMuc());
            existing.setThuongHieu(sanPham.getThuongHieu());
            existing.setChatLieu(sanPham.getChatLieu());
            existing.setTrangThai(sanPham.getTrangThai());
            existing.setUpdatedAt(LocalDateTime.now());

            SanPham updated = sanPhamRepository.save(existing);
            log.info("Đã cập nhật sản phẩm: {}", updated.getTen());
            return updated;
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật sản phẩm: ", e);
            return null;
        }
    }

    /**
     * Xóa sản phẩm (soft delete)
     */
    @Transactional
    public boolean xoaSanPham(Integer id) {
        try {
            SanPham sanPham = sanPhamRepository.findById(id).orElse(null);
            if (sanPham == null) {
                log.warn("Không tìm thấy sản phẩm với ID: {}", id);
                return false;
            }

            sanPham.setTrangThai((byte) 0);
            sanPham.setUpdatedAt(LocalDateTime.now());
            sanPhamRepository.save(sanPham);
            log.info("Đã xóa sản phẩm: {}", sanPham.getTen());
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi xóa sản phẩm: ", e);
            return false;
        }
    }

    /**
     * Thêm biến thể sản phẩm
     */
    @Transactional
    public SanPhamChiTiet themBienThe(SanPhamChiTiet bienThe) {
        try {
            // Kiểm tra trùng lặp
            if (sanPhamChiTietRepository.findBySanPhamSanPhamIdAndMauSacMauSacIdAndKichThuocKichThuocId(
                    bienThe.getSanPham().getSanPhamId(),
                    bienThe.getMauSac().getMauSacId(),
                    bienThe.getKichThuoc().getKichThuocId()).isPresent()) {
                log.warn("Biến thể đã tồn tại");
                return null;
            }

            bienThe.setTrangThai((byte) 1);
            bienThe.setCreatedAt(LocalDateTime.now());

            SanPhamChiTiet saved = sanPhamChiTietRepository.save(bienThe);
            log.info("Đã thêm biến thể mới cho sản phẩm: {}", bienThe.getSanPham().getTen());
            return saved;
        } catch (Exception e) {
            log.error("Lỗi khi thêm biến thể: ", e);
            return null;
        }
    }

    /**
     * Cập nhật biến thể
     */
    @Transactional
    public SanPhamChiTiet capNhatBienThe(Integer variantId, SanPhamChiTiet bienThe) {
        try {
            SanPhamChiTiet existing = sanPhamChiTietRepository.findById(variantId).orElse(null);
            if (existing == null) {
                log.warn("Không tìm thấy biến thể với ID: {}", variantId);
                return null;
            }

            existing.setGiaBan(bienThe.getGiaBan());
            existing.setGiaGoc(bienThe.getGiaGoc());
            existing.setSoLuongTon(bienThe.getSoLuongTon());
            existing.setHinhAnh(bienThe.getHinhAnh());
            existing.setTrangThai(bienThe.getTrangThai());
            existing.setUpdatedAt(LocalDateTime.now());

            SanPhamChiTiet updated = sanPhamChiTietRepository.save(existing);
            log.info("Đã cập nhật biến thể ID: {}", variantId);
            return updated;
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật biến thể: ", e);
            return null;
        }
    }

    /**
     * Xóa biến thể
     */
    @Transactional
    public boolean xoaBienThe(Integer variantId) {
        try {
            SanPhamChiTiet bienThe = sanPhamChiTietRepository.findById(variantId).orElse(null);
            if (bienThe == null) {
                log.warn("Không tìm thấy biến thể với ID: {}", variantId);
                return false;
            }

            bienThe.setTrangThai((byte) 0);
            bienThe.setUpdatedAt(LocalDateTime.now());
            sanPhamChiTietRepository.save(bienThe);
            log.info("Đã xóa biến thể ID: {}", variantId);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi xóa biến thể: ", e);
            return false;
        }
    }

    /**
     * Cập nhật số lượng tồn kho
     */
    @Transactional
    public boolean capNhatSoLuongTon(Integer variantId, Integer soLuong) {
        try {
            SanPhamChiTiet bienThe = sanPhamChiTietRepository.findById(variantId).orElse(null);
            if (bienThe == null) {
                log.warn("Không tìm thấy biến thể với ID: {}", variantId);
                return false;
            }

            bienThe.setSoLuongTon(soLuong);
            bienThe.setUpdatedAt(LocalDateTime.now());
            sanPhamChiTietRepository.save(bienThe);
            log.info("Đã cập nhật số lượng tồn kho biến thể ID {}: {}", variantId, soLuong);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật số lượng tồn kho: ", e);
            return false;
        }
    }

    /**
     * Giảm số lượng tồn kho (khi bán hàng)
     */
    @Transactional
    public boolean giamSoLuongTon(Integer variantId, Integer soLuong) {
        try {
            SanPhamChiTiet bienThe = sanPhamChiTietRepository.findById(variantId).orElse(null);
            if (bienThe == null) {
                log.warn("Không tìm thấy biến thể với ID: {}", variantId);
                return false;
            }

            if (bienThe.getSoLuongTon() < soLuong) {
                log.warn("Không đủ số lượng tồn kho. Tồn: {}, Yêu cầu: {}",
                        bienThe.getSoLuongTon(), soLuong);
                return false;
            }

            bienThe.setSoLuongTon(bienThe.getSoLuongTon() - soLuong);
            bienThe.setUpdatedAt(LocalDateTime.now());
            sanPhamChiTietRepository.save(bienThe);
            log.info("Đã giảm số lượng tồn kho biến thể ID {}: -{}", variantId, soLuong);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi giảm số lượng tồn kho: ", e);
            return false;
        }
    }

    /**
     * Tăng số lượng tồn kho (khi hoàn trả)
     */
    @Transactional
    public boolean tangSoLuongTon(Integer variantId, Integer soLuong) {
        try {
            SanPhamChiTiet bienThe = sanPhamChiTietRepository.findById(variantId).orElse(null);
            if (bienThe == null) {
                log.warn("Không tìm thấy biến thể với ID: {}", variantId);
                return false;
            }

            bienThe.setSoLuongTon(bienThe.getSoLuongTon() + soLuong);
            bienThe.setUpdatedAt(LocalDateTime.now());
            sanPhamChiTietRepository.save(bienThe);
            log.info("Đã tăng số lượng tồn kho biến thể ID {}: +{}", variantId, soLuong);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi tăng số lượng tồn kho: ", e);
            return false;
        }
    }

    /**
     * Kiểm tra tồn kho
     */
    @Transactional(readOnly = true)
    public boolean kiemTraTonKho(Integer variantId, Integer soLuong) {
        try {
            SanPhamChiTiet bienThe = sanPhamChiTietRepository.findById(variantId).orElse(null);
            if (bienThe == null) {
                return false;
            }
            return bienThe.getSoLuongTon() >= soLuong;
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra tồn kho: ", e);
            return false;
        }
    }

    /**
     * Lấy tổng số lượng tồn kho của sản phẩm
     */
    @Transactional(readOnly = true)
    public Integer layTongSoLuongTon(Integer sanPhamId) {
        try {
            List<SanPhamChiTiet> bienThes = sanPhamChiTietRepository.findBySanPhamSanPhamId(sanPhamId);
            return bienThes.stream()
                    .mapToInt(SanPhamChiTiet::getSoLuongTon)
                    .sum();
        } catch (Exception e) {
            log.error("Lỗi khi lấy tổng số lượng tồn: ", e);
            return 0;
        }
    }

    /**
     * Lấy giá thấp nhất của sản phẩm
     */
    @Transactional(readOnly = true)
    public BigDecimal layGiaThapNhat(Integer sanPhamId) {
        try {
            List<SanPhamChiTiet> bienThes = sanPhamChiTietRepository.findBySanPhamSanPhamId(sanPhamId);
            return bienThes.stream()
                    .map(SanPhamChiTiet::getGiaBan)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        } catch (Exception e) {
            log.error("Lỗi khi lấy giá thấp nhất: ", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Lấy giá cao nhất của sản phẩm
     */
    @Transactional(readOnly = true)
    public BigDecimal layGiaCaoNhat(Integer sanPhamId) {
        try {
            List<SanPhamChiTiet> bienThes = sanPhamChiTietRepository.findBySanPhamSanPhamId(sanPhamId);
            return bienThes.stream()
                    .map(SanPhamChiTiet::getGiaBan)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        } catch (Exception e) {
            log.error("Lỗi khi lấy giá cao nhất: ", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Đếm số lượng sản phẩm
     */
    @Transactional(readOnly = true)
    public long demSoLuongSanPham() {
        try {
            return sanPhamRepository.count();
        } catch (Exception e) {
            log.error("Lỗi khi đếm số lượng sản phẩm: ", e);
            return 0;
        }
    }

    /**
     * Đếm số lượng biến thể
     */
    @Transactional(readOnly = true)
    public long demSoLuongBienThe() {
        try {
            return sanPhamChiTietRepository.count();
        } catch (Exception e) {
            log.error("Lỗi khi đếm số lượng biến thể: ", e);
            return 0;
        }
    }

    /**
     * Lấy sản phẩm sắp hết hàng
     */
    @Transactional(readOnly = true)
    public List<SanPhamChiTiet> laySanPhamSapHetHang(int threshold) {
        try {
            return sanPhamChiTietRepository.findLowStockVariants(threshold);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm sắp hết hàng: ", e);
            return List.of();
        }
    }

    /**
     * Lấy sản phẩm hết hàng
     */
    @Transactional(readOnly = true)
    public List<SanPhamChiTiet> laySanPhamHetHang() {
        try {
            return sanPhamChiTietRepository.findOutOfStockVariants();
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm hết hàng: ", e);
            return List.of();
        }
    }

    /**
     * Lọc sản phẩm nâng cao
     */
    @Transactional(readOnly = true)
    public Page<SanPham> locSanPham(
            Integer danhMucId,
            Integer thuongHieuId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {
        try {
            return sanPhamRepository.filterProducts(danhMucId, thuongHieuId, minPrice, maxPrice, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lọc sản phẩm: ", e);
            return Page.empty();
        }
    }

    /**
     * Kiểm tra sản phẩm có tồn tại không
     */
    @Transactional(readOnly = true)
    public boolean kiemTraTonTai(Integer id) {
        try {
            return sanPhamRepository.existsById(id);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra tồn tại sản phẩm: ", e);
            return false;
        }
    }
}
