package com.poly.service;

import com.poly.entity.SanPham;
import com.poly.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service xử lý logic nghiệp vụ cho Sản phẩm
 *
 * @author Nhóm 132
 */
@Service
public class SanPhamService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    /**
     * Lấy danh sách sản phẩm nổi bật
     * Mặc định lấy 8 sản phẩm mới nhất
     */
    public List<SanPham> getFeaturedProducts() {
        return getFeaturedProducts(8);
    }

    /**
     * ✅ FIXED: Lấy danh sách sản phẩm nổi bật với số lượng tùy chỉnh
     * @param limit Số lượng sản phẩm cần lấy
     */
    public List<SanPham> getFeaturedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        // ✅ Gọi method trả về List (không phải Page)
        return sanPhamRepository.findFeaturedProducts(pageable);
    }

    /**
     * ✅ FIXED: Lấy sản phẩm có giảm giá cao nhất
     * @param limit Số lượng sản phẩm
     */
    public List<SanPham> getTopDiscountedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        // ✅ Gọi method trả về List (không phải Page)
        return sanPhamRepository.findTopDiscountedProducts(pageable);
    }

    /**
     * ✅ FIXED: Lấy sản phẩm mới nhất
     * @param limit Số lượng sản phẩm
     *
     * QUAN TRỌNG: Repository PHẢI có 2 versions của method này:
     * - findNewestProducts(Pageable) → List<SanPham> (cho fixed size)
     * - findNewestProductsPage(Pageable) → Page<SanPham> (cho pagination)
     */
    public List<SanPham> getNewestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        // Option 1: Nếu Repository có method trả về List
        return sanPhamRepository.findFeaturedProducts(pageable);

        // Option 2: Nếu Repository chỉ có method trả về Page
        // Page<SanPham> page = sanPhamRepository.findNewestProducts(pageable);
        // return page.getContent();  // Convert Page to List
    }

    /**
     * ✅ THÊM: Lấy sản phẩm mới nhất với phân trang (cho category page)
     * @param pageable Thông tin phân trang
     * @return Page với metadata
     */
    public Page<SanPham> getNewestProductsPage(Pageable pageable) {
        return sanPhamRepository.findNewestProducts(pageable);
    }

    /**
     * ✅ THÊM: Lấy sản phẩm random (bán chạy giả lập)
     * @param limit Số lượng sản phẩm
     */
    public List<SanPham> getRandomProducts(int limit) {
        return sanPhamRepository.findRandomProducts(limit);
    }

    /**
     * ✅ THÊM: Tìm sản phẩm theo ID
     * @param id ID sản phẩm
     */
    public SanPham findById(Integer id) {
        return sanPhamRepository.findBySanPhamIdAndTrangThai(id, 1)
                .orElse(null);
    }

    /**
     * ✅ THÊM: Lấy tất cả sản phẩm đang hoạt động
     * @param pageable Thông tin phân trang
     */
    public Page<SanPham> getAllActiveProducts(Pageable pageable) {
        return sanPhamRepository.findByTrangThai(1, pageable);
    }

    /**
     * ✅ THÊM: Tìm kiếm sản phẩm
     * @param keyword Từ khóa tìm kiếm
     * @param pageable Thông tin phân trang
     */
    public Page<SanPham> searchProducts(String keyword, Pageable pageable) {
        return sanPhamRepository.searchProducts(keyword, pageable);
    }
}