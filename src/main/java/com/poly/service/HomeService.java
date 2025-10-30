package com.poly.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.dto.ProductHomeDTO;
import com.poly.entity.DanhMuc;
import com.poly.repository.DanhMucRepository;
import com.poly.repository.SanPhamRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;

    /**
     * Lấy danh sách sản phẩm cho trang chủ
     */
    public Page<ProductHomeDTO> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.findAllForHomePage(pageable);
    }

    /**
     * Lấy sản phẩm theo danh mục
     */
    public Page<ProductHomeDTO> getProductsByCategory(Integer danhMucId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.findByDanhMucId(danhMucId, pageable);
    }

    /**
     * Tìm kiếm sản phẩm
     */
    public Page<ProductHomeDTO> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.searchProducts(keyword, pageable);
    }

    /**
     * Lấy danh sách sản phẩm nổi bật (top 8)
     */
    public List<ProductHomeDTO> getFeaturedProducts() {
        Pageable pageable = PageRequest.of(0, 8);
        return sanPhamRepository.findFeaturedProducts(pageable);
    }

    /**
     * Lấy tất cả danh mục đang hoạt động
     */
    public List<DanhMuc> getAllActiveCategories() {
        return danhMucRepository.findByTrangThaiOrderByTenAsc((byte) 1);
    }

    /**
     * Lấy danh mục theo ID
     */
    public DanhMuc getCategoryById(Integer id) {
        return danhMucRepository.findById(id).orElse(null);
    }
}