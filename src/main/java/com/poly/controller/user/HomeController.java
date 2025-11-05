package com.poly.controller.user;

import com.poly.entity.ThuongHieu;
import com.poly.repository.ThuongHieuRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.dto.ProductHomeDTO;
import com.poly.entity.DanhMuc;
import com.poly.service.HomeService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    /**
     * Trang chủ
     */
    @GetMapping({"/", "/Index"})
    public String home(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String search,
            Model model
    ) {
        // Lấy danh sách danh mục
        List<DanhMuc> categories = homeService.getAllActiveCategories();
        model.addAttribute("categories", categories);

        // ============================================
        // LẤY DANH SÁCH THƯƠNG HIỆU TỪ DATABASE - FIXED!
        // ============================================
        List<ThuongHieu> brands = thuongHieuRepository.findByTrangThai((byte) 1);
        model.addAttribute("brands", brands);  // ← THÊM VÀO MODEL

        // Lấy sản phẩm nổi bật cho trang chủ
        if (page == 0 && category == null && search == null) {
            List<ProductHomeDTO> featuredProducts = homeService.getFeaturedProducts();
            model.addAttribute("featuredProducts", featuredProducts);
        }

        // Lấy danh sách sản phẩm
        Page<ProductHomeDTO> productsPage;

        if (search != null && !search.trim().isEmpty()) {
            // Tìm kiếm sản phẩm
            productsPage = homeService.searchProducts(search.trim(), page, size);
            model.addAttribute("search", search);
            model.addAttribute("pageTitle", "Kết quả tìm kiếm: " + search);
        } else if (category != null) {
            // Lọc theo danh mục
            productsPage = homeService.getProductsByCategory(category, page, size);
            DanhMuc selectedCategory = homeService.getCategoryById(category);
            model.addAttribute("selectedCategory", selectedCategory);
            model.addAttribute("pageTitle", selectedCategory != null ? selectedCategory.getTen() : "Sản phẩm");
        } else {
            // Tất cả sản phẩm
            productsPage = homeService.getAllProducts(page, size);
            model.addAttribute("pageTitle", "Bộ sưu tập giày mới");
        }

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("category", category);

        return "/user/index";
    }

    /**
     * API endpoint để lấy sản phẩm (cho AJAX/SPA)
     */
    @GetMapping("/api/products")
    @ResponseBody
    public Page<ProductHomeDTO> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String search
    ) {
        if (search != null && !search.trim().isEmpty()) {
            return homeService.searchProducts(search.trim(), page, size);
        } else if (category != null) {
            return homeService.getProductsByCategory(category, page, size);
        } else {
            return homeService.getAllProducts(page, size);
        }
    }

    /**
     * API endpoint để lấy danh mục
     */
    @GetMapping("/api/categories")
    @ResponseBody
    public List<DanhMuc> getCategories() {
        return homeService.getAllActiveCategories();
    }

    /**
     * API endpoint để lấy thương hiệu
     */
    @GetMapping("/api/brands")
    @ResponseBody
    public List<ThuongHieu> getBrands() {
        return thuongHieuRepository.findByTrangThai((byte) 1);
    }

    @GetMapping("/cart")
    public String cartPage() {
        return "Cart-page";
    }
}