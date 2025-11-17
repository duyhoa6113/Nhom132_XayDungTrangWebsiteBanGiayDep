package com.poly.controller.user;

import com.poly.entity.DanhMuc;
import com.poly.entity.KhachHang;
import com.poly.entity.SanPham;
import com.poly.entity.ThuongHieu;
import com.poly.repository.DanhMucRepository;
import com.poly.repository.SanPhamRepository;
import com.poly.repository.ThuongHieuRepository;
import com.poly.service.CartService;
import com.poly.service.SanPhamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
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

    @Autowired
    private CartService cartService;

    @GetMapping({"/Index"})
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String search,
            Model model,
            HttpSession session) {

        System.out.println("========== DEBUG PAGINATION ==========");
        System.out.println("Page: " + page);
        System.out.println("Size: " + size);
        System.out.println("Category: " + category);
        System.out.println("Search: " + search);

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

        System.out.println("Total Elements: " + productsPage.getTotalElements());
        System.out.println("Total Pages: " + productsPage.getTotalPages());
        System.out.println("Content Size: " + productsPage.getContent().size());
        System.out.println("======================================");

        // Lấy sản phẩm nổi bật
        List<SanPham> featuredProducts = sanPhamService.getFeaturedProducts(6);
        model.addAttribute("featuredProducts", featuredProducts);

        // Lấy danh mục và thương hiệu
        List<DanhMuc> categories = danhMucRepository.findByTrangThai(1);
        List<ThuongHieu> brands = thuongHieuRepository.findByTrangThai(1);

        // Lấy cart count
        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
            if (khachHang != null) {
                Integer cartCount = cartService.getCartCount(khachHang);
                model.addAttribute("cartCount", cartCount != null ? cartCount : 0);
            } else {
                model.addAttribute("cartCount", 0);
            }
        } catch (Exception e) {
            model.addAttribute("cartCount", 0);
        }

        // Logic phân trang
        int totalPages = productsPage.getTotalPages();
        int currentPage = page;
        List<Integer> pageNumbers = new ArrayList<>();
        int maxPagesToShow = 7;

        if (totalPages <= maxPagesToShow) {
            for (int i = 0; i < totalPages; i++) {
                pageNumbers.add(i);
            }
        } else {
            int startPage, endPage;
            if (currentPage <= 3) {
                startPage = 0;
                endPage = 4;
            } else if (currentPage >= totalPages - 4) {
                startPage = totalPages - 5;
                endPage = totalPages - 1;
            } else {
                startPage = currentPage - 1;
                endPage = currentPage + 1;
            }

            pageNumbers.add(0);
            if (startPage > 1) {
                pageNumbers.add(-1);
            }
            for (int i = startPage; i <= endPage; i++) {
                if (i > 0 && i < totalPages - 1) {
                    pageNumbers.add(i);
                }
            }
            if (endPage < totalPages - 2) {
                pageNumbers.add(-1);
            }
            if (totalPages > 1) {
                pageNumbers.add(totalPages - 1);
            }
        }

        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", productsPage.getTotalElements());
        model.addAttribute("productsPage", productsPage);
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        model.addAttribute("currentPage", page);

        return "user/Index";
    }
}