package com.poly.service;

import com.poly.entity.DanhMuc;
import com.poly.entity.SanPham;
import com.poly.entity.ThuongHieu;
import com.poly.repository.DanhMucRepository;
import com.poly.repository.SanPhamRepository;
import com.poly.repository.ThuongHieuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HomeService {

    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final ThuongHieuRepository thuongHieuRepository;

    /**
     * Lấy danh sách sản phẩm cho trang chủ với phân trang
     */
    public Page<SanPham> getAllProducts(int page, int size) {
        try {
            // BỎ Sort.by ở đây vì query đã có ORDER BY
            Pageable pageable = PageRequest.of(page, size);
            return sanPhamRepository.findAllWithDetails(pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách sản phẩm: ", e);
            return Page.empty();
        }
    }

    /**
     * Lấy sản phẩm theo danh mục
     */
    public Page<SanPham> getProductsByCategory(Integer danhMucId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return sanPhamRepository.findByDanhMucDanhMucId(danhMucId, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm theo danh mục: ", e);
            return Page.empty();
        }
    }

    /**
     * Lấy sản phẩm theo thương hiệu
     */
    public Page<SanPham> getProductsByBrand(Integer thuongHieuId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return sanPhamRepository.findByThuongHieuThuongHieuId(thuongHieuId, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm theo thương hiệu: ", e);
            return Page.empty();
        }
    }

    /**
     * Tìm kiếm sản phẩm
     */
    public Page<SanPham> searchProducts(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return sanPhamRepository.searchProducts(keyword, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm sản phẩm: ", e);
            return Page.empty();
        }
    }

    /**
     * Lọc sản phẩm nâng cao
     */
    public Page<SanPham> filterProducts(
            Integer danhMucId,
            Integer thuongHieuId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy) {
        try {
            Pageable pageable;
            if (sortBy != null && !sortBy.isEmpty()) {
                Sort sort = getSortOption(sortBy);
                pageable = PageRequest.of(page, size, sort);
            } else {
                pageable = PageRequest.of(page, size);
            }
            return sanPhamRepository.filterProducts(danhMucId, thuongHieuId, minPrice, maxPrice, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lọc sản phẩm: ", e);
            return Page.empty();
        }
    }

    /**
     * Lấy sản phẩm theo khoảng giá
     */
    public Page<SanPham> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return sanPhamRepository.findByPriceRange(minPrice, maxPrice, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm theo khoảng giá: ", e);
            return Page.empty();
        }
    }

    /**
     * Lấy danh sách sản phẩm nổi bật (top 8)
     */
    public List<SanPham> getFeaturedProducts(int limit) {
        try {
            return sanPhamRepository.findLatestProducts(limit);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm nổi bật: ", e);
            return List.of();
        }
    }

    /**
     * Lấy sản phẩm mới nhất
     */
    public List<SanPham> getLatestProducts(int limit) {
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
    public List<SanPham> getBestSellingProducts(int limit) {
        try {
            return sanPhamRepository.findBestSellingProducts(limit);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm bán chạy: ", e);
            return List.of();
        }
    }

    /**
     * Lấy sản phẩm đang giảm giá
     */
    public Page<SanPham> getDiscountedProducts(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return sanPhamRepository.findDiscountedProducts(pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm giảm giá: ", e);
            return Page.empty();
        }
    }

    /**
     * Lấy sản phẩm liên quan
     */
    public List<SanPham> getRelatedProducts(Integer danhMucId, Integer excludeId, int limit) {
        try {
            return sanPhamRepository.findRelatedProducts(danhMucId, excludeId, limit);
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm liên quan: ", e);
            return List.of();
        }
    }

    /**
     * Lấy tất cả danh mục đang hoạt động
     */
    public List<DanhMuc> getAllActiveCategories() {
        try {
            return danhMucRepository.findByTrangThai((byte) 1);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh mục: ", e);
            return List.of();
        }
    }

    /**
     * Lấy tất cả thương hiệu đang hoạt động
     */
    public List<ThuongHieu> getAllActiveBrands() {
        try {
            return thuongHieuRepository.findByTrangThai((byte) 1);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thương hiệu: ", e);
            return List.of();
        }
    }

    /**
     * Lấy danh mục theo ID
     */
    public DanhMuc getCategoryById(Integer id) {
        try {
            return danhMucRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh mục theo ID: ", e);
            return null;
        }
    }

    /**
     * Lấy thương hiệu theo ID
     */
    public ThuongHieu getBrandById(Integer id) {
        try {
            return thuongHieuRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thương hiệu theo ID: ", e);
            return null;
        }
    }

    /**
     * Đếm tổng số sản phẩm
     */
    public long getTotalProducts() {
        try {
            return sanPhamRepository.count();
        } catch (Exception e) {
            log.error("Lỗi khi đếm sản phẩm: ", e);
            return 0;
        }
    }

    /**
     * Lấy option sắp xếp
     */
    private Sort getSortOption(String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Sort.unsorted();
        }

        return switch (sortBy) {
            case "name-asc" -> Sort.by("ten").ascending();
            case "name-desc" -> Sort.by("ten").descending();
            case "newest" -> Sort.by("createdAt").descending();
            case "oldest" -> Sort.by("createdAt").ascending();
            // Không sort theo giá vì phức tạp với relationship
            default -> Sort.unsorted();
        };
    }

    /**
     * Lấy thống kê trang chủ
     */
    public HomeStatistics getHomeStatistics() {
        try {
            HomeStatistics stats = new HomeStatistics();
            stats.setTotalProducts(sanPhamRepository.count());
            stats.setTotalCategories(danhMucRepository.count());
            stats.setTotalBrands(thuongHieuRepository.count());
            return stats;
        } catch (Exception e) {
            log.error("Lỗi khi lấy thống kê: ", e);
            return new HomeStatistics();
        }
    }

    /**
     * Inner class cho thống kê
     */
    public static class HomeStatistics {
        private long totalProducts;
        private long totalCategories;
        private long totalBrands;

        public long getTotalProducts() {
            return totalProducts;
        }

        public void setTotalProducts(long totalProducts) {
            this.totalProducts = totalProducts;
        }

        public long getTotalCategories() {
            return totalCategories;
        }

        public void setTotalCategories(long totalCategories) {
            this.totalCategories = totalCategories;
        }

        public long getTotalBrands() {
            return totalBrands;
        }

        public void setTotalBrands(long totalBrands) {
            this.totalBrands = totalBrands;
        }
    }
}
