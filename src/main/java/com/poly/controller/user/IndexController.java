package com.poly.controller.user;

import com.poly.entity.DanhMuc;
import com.poly.entity.SanPham;
import com.poly.entity.ThuongHieu;
import com.poly.repository.DanhMucRepository;
import com.poly.repository.SanPhamRepository;
import com.poly.repository.ThuongHieuRepository;
import com.poly.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller xử lý trang chủ
 *
 * @author Nhóm 132
 */
@Controller
public class IndexController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Autowired
    private SanPhamService sanPhamService;

    @GetMapping({ "/home","/Index"})
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SanPham> productsPage;

        // Xử lý tìm kiếm
        if (search != null && !search.trim().isEmpty()) {
            productsPage = sanPhamRepository.searchProducts(search.trim(), pageable);
            model.addAttribute("pageTitle", "Kết quả tìm kiếm: " + search);
            model.addAttribute("search", search);
        }
        // Xử lý lọc theo danh mục
        else if (category != null) {
            productsPage = sanPhamRepository.findByDanhMuc_DanhMucIdAndTrangThai(category, 1, pageable);
            DanhMuc selectedCategory = danhMucRepository.findById(category).orElse(null);
            model.addAttribute("selectedCategory", selectedCategory);
            model.addAttribute("pageTitle", selectedCategory != null ? selectedCategory.getTen() : "Sản phẩm");
            model.addAttribute("category", category);
        }
        // Hiển thị tất cả sản phẩm
        else {
            productsPage = sanPhamRepository.findByTrangThai(1, pageable);
            model.addAttribute("pageTitle", "Tất Cả Sản Phẩm");
        }

        // ===== THÊM PHẦN NÀY - Lấy sản phẩm nổi bật =====
        List<SanPham> featuredProducts = sanPhamService.getFeaturedProducts(4);
        model.addAttribute("featuredProducts", featuredProducts);
        // ================================================

        // Lấy danh mục và thương hiệu
        List<DanhMuc> categories = danhMucRepository.findByTrangThai(1);
        List<ThuongHieu> brands = thuongHieuRepository.findByTrangThai(1);

        // Đưa dữ liệu vào model
        model.addAttribute("productsPage", productsPage);
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        model.addAttribute("currentPage", page);

        return "user/Index";
    }
}