package com.poly.controller.user;

import com.poly.entity.DanhMuc;
import com.poly.entity.KhachHang;
import com.poly.entity.SanPham;
import com.poly.entity.ThuongHieu;
import com.poly.service.HomeService;
import com.poly.service.YeuThichService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final HomeService homeService;
    private final YeuThichService yeuThichService;

    @GetMapping({"/", "/Index"})
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer brand,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            HttpSession session,
            Model model) {

        try {
            // Lấy thông tin khách hàng từ session
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            // Lấy danh sách danh mục và thương hiệu
            List<DanhMuc> categories = homeService.getAllActiveCategories();
            List<ThuongHieu> brands = homeService.getAllActiveBrands();
            model.addAttribute("categories", categories);
            model.addAttribute("brands", brands);

            // Lấy sản phẩm theo điều kiện
            Page<SanPham> productsPage;
            String pageTitle = "Tất Cả Sản Phẩm";

            if (search != null && !search.trim().isEmpty()) {
                // Tìm kiếm
                productsPage = homeService.searchProducts(search, page, size);
                pageTitle = "Kết quả tìm kiếm: " + search;
                model.addAttribute("search", search);
            } else if (category != null || brand != null || minPrice != null || maxPrice != null) {
                // Lọc nâng cao
                productsPage = homeService.filterProducts(category, brand, minPrice, maxPrice, page, size, sort);

                if (category != null) {
                    DanhMuc selectedCategory = homeService.getCategoryById(category);
                    if (selectedCategory != null) {
                        pageTitle = selectedCategory.getTen();
                        model.addAttribute("selectedCategory", selectedCategory);
                    }
                }

                if (brand != null) {
                    ThuongHieu selectedBrand = homeService.getBrandById(brand);
                    if (selectedBrand != null) {
                        model.addAttribute("selectedBrand", selectedBrand);
                    }
                }

                model.addAttribute("category", category);
                model.addAttribute("brand", brand);
                model.addAttribute("minPrice", minPrice);
                model.addAttribute("maxPrice", maxPrice);
            } else {
                // Lấy tất cả
                productsPage = homeService.getAllProducts(page, size);
            }

            // Thêm danh sách ID sản phẩm yêu thích nếu đã đăng nhập
            if (khachHang != null) {
                List<Integer> wishlistIds = yeuThichService.layDanhSachIdYeuThich(khachHang.getKhachHangId());
                model.addAttribute("wishlistIds", wishlistIds);

                long wishlistCount = yeuThichService.demSoLuongYeuThich(khachHang);
                model.addAttribute("wishlistCount", wishlistCount);

                log.debug("Khách hàng {} có {} sản phẩm yêu thích",
                        khachHang.getKhachHangId(), wishlistCount);
            } else {
                model.addAttribute("wishlistIds", List.of());
                model.addAttribute("wishlistCount", 0);
            }

            // Lấy sản phẩm nổi bật cho sidebar
            List<SanPham> featuredProducts = homeService.getFeaturedProducts(4);
            model.addAttribute("featuredProducts", featuredProducts);

            // Thêm vào model
            model.addAttribute("productsPage", productsPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageTitle", pageTitle);
            model.addAttribute("sort", sort);

            // Thống kê
            HomeService.HomeStatistics stats = homeService.getHomeStatistics();
            model.addAttribute("stats", stats);

            return "user/Index";
        } catch (Exception e) {
            log.error("Lỗi khi tải trang chủ: ", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải trang");
            return "error";
        }
    }

    /**
     * Trang sản phẩm mới
     */
    @GetMapping("/new-arrivals")
    public String newArrivals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            HttpSession session,
            Model model) {

        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            Page<SanPham> productsPage = homeService.getAllProducts(page, size);

            if (khachHang != null) {
                List<Integer> wishlistIds = yeuThichService.layDanhSachIdYeuThich(khachHang.getKhachHangId());
                model.addAttribute("wishlistIds", wishlistIds);
            }

            model.addAttribute("productsPage", productsPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageTitle", "Sản Phẩm Mới");

            return "new-arrivals";
        } catch (Exception e) {
            log.error("Lỗi khi tải trang sản phẩm mới: ", e);
            return "redirect:/user/Index";
        }
    }

    /**
     * Trang sản phẩm giảm giá
     */
    @GetMapping("/sale")
    public String saleProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            HttpSession session,
            Model model) {

        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            Page<SanPham> productsPage = homeService.getDiscountedProducts(page, size);

            if (khachHang != null) {
                List<Integer> wishlistIds = yeuThichService.layDanhSachIdYeuThich(khachHang.getKhachHangId());
                model.addAttribute("wishlistIds", wishlistIds);
            }

            model.addAttribute("productsPage", productsPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageTitle", "Sản Phẩm Giảm Giá");

            return "sale";
        } catch (Exception e) {
            log.error("Lỗi khi tải trang giảm giá: ", e);
            return "redirect:/user/Index";
        }
    }

    /**
     * Trang giới thiệu
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }

    /**
     * Trang liên hệ
     */
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}
