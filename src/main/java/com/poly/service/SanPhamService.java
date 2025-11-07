package com.poly.service;

import com.poly.entity.SanPham;
import com.poly.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Lấy danh sách sản phẩm nổi bật với số lượng tùy chỉnh
     * @param limit Số lượng sản phẩm cần lấy
     */
    public List<SanPham> getFeaturedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return sanPhamRepository.findFeaturedProducts(pageable);
    }

    /**
     * Lấy sản phẩm có giảm giá cao nhất
     * @param limit Số lượng sản phẩm
     */
    public List<SanPham> getTopDiscountedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return sanPhamRepository.findTopDiscountedProducts(pageable);
    }

    /**
     * Lấy sản phẩm mới nhất
     * @param limit Số lượng sản phẩm
     */
    public List<SanPham> getNewestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return sanPhamRepository.findNewestProducts(pageable);
    }
}