package com.poly.service;

import com.poly.dto.BrandWithCount;
import com.poly.dto.CategoryWithCount;
import com.poly.entity.*;
import com.poly.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private ChatLieuRepository chatLieuRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    /**
     * Lấy thông tin danh mục theo ID
     */
    public DanhMuc getCategoryById(Integer categoryId) {
        return danhMucRepository.findById(categoryId)
                .filter(dm -> dm.getTrangThai() == 1)
                .orElse(null);
    }

    /**
     * Lấy tất cả danh mục đang hoạt động
     */
    public List<DanhMuc> getAllActiveCategories() {
        return danhMucRepository.findByTrangThaiOrderByTenAsc(1);
    }

    /**
     * Lấy tất cả danh mục kèm số lượng sản phẩm
     */
    public List<CategoryWithCount> getAllCategoriesWithCount() {
        return danhMucRepository.findAllCategoriesWithProductCount();
    }

    /**
     * Lấy danh sách thương hiệu có trong danh mục
     */
    public List<BrandWithCount> getBrandsByCategory(Integer categoryId) {
        return thuongHieuRepository.findBrandsByCategoryWithCount(categoryId);
    }

    /**
     * Lấy tất cả thương hiệu đang hoạt động
     */
    public List<ThuongHieu> getAllActiveBrands() {
        return thuongHieuRepository.findByTrangThaiOrderByTenAsc(1);
    }

    /**
     * Lấy tất cả size đang hoạt động
     */
    public List<KichThuoc> getAllActiveSizes() {
        return kichThuocRepository.findByTrangThaiOrderByTenAsc(1);
    }

    /**
     * Lấy tất cả màu sắc đang hoạt động
     */
    public List<MauSac> getAllActiveColors() {
        return mauSacRepository.findByTrangThaiOrderByTenAsc(1);
    }

    /**
     * Lấy danh sách chất liệu có trong danh mục
     */
    public List<ChatLieu> getMaterialsByCategory(Integer categoryId) {
        return chatLieuRepository.findMaterialsByCategoryAndActive(categoryId);
    }

    /**
     * Lấy tất cả chất liệu đang hoạt động
     */
    public List<ChatLieu> getAllActiveMaterials() {
        return chatLieuRepository.findByTrangThaiOrderByTenAsc(1);
    }

    /**
     * Đếm số sản phẩm trong danh mục
     */
    public long countProductsByCategory(Integer categoryId) {
        return sanPhamRepository.countByDanhMucIdAndTrangThai(categoryId, 1);
    }
}