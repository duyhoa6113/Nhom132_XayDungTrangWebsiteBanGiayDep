package com.poly.controller.user;

import com.poly.service.CategoryService;
import com.poly.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    /**
     * Hiển thị trang danh mục
     * URL: /category/{categoryId}
     */
    @GetMapping("/{categoryId}")
    public String showCategory(
            @PathVariable("categoryId") Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "popular") String sort,
            @RequestParam(required = false) List<Integer> brand,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) List<Integer> size_filter,
            @RequestParam(required = false) List<Integer> color,
            @RequestParam(required = false) List<Integer> material,
            @RequestParam(required = false) Integer rating,
            Model model
    ) {

        // 1. Lấy thông tin danh mục
        var category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            return "redirect:/";
        }
        model.addAttribute("category", category);

        // 2. Lấy tất cả danh mục cho sidebar
        var allCategories = categoryService.getAllActiveCategories();
        model.addAttribute("allCategories", allCategories);

        // 3. Lấy danh sách thương hiệu có trong danh mục này
        var brands = categoryService.getBrandsByCategory(categoryId);
        model.addAttribute("brands", brands);

        // 4. Lấy danh sách size
        var sizes = categoryService.getAllActiveSizes();
        model.addAttribute("sizes", sizes);

        // 5. Lấy danh sách màu sắc
        var colors = categoryService.getAllActiveColors();
        model.addAttribute("colors", colors);

        // 6. Lấy danh sách chất liệu có trong danh mục
        var materials = categoryService.getMaterialsByCategory(categoryId);
        model.addAttribute("materials", materials);

        // 7. Xác định Sort
        Sort sorting = getSortOption(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);

        // 8. Lọc sản phẩm theo các tiêu chí
        Page<?> productsPage = productService.filterProducts(
                categoryId,
                brand,
                priceRange,
                size_filter,
                color,
                material,
                rating,
                pageable
        );

        // 9. Đếm tổng số sản phẩm trong danh mục
        long totalProducts = productService.countProductsByCategory(categoryId);

        // 10. Add vào model
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("totalElements", productsPage.getTotalElements());

        // 11. Giữ lại filter đã chọn
        model.addAttribute("selectedBrands", brand);
        model.addAttribute("selectedPriceRange", priceRange);
        model.addAttribute("selectedSizes", size_filter);
        model.addAttribute("selectedColors", color);
        model.addAttribute("selectedMaterials", material);
        model.addAttribute("selectedRating", rating);
        model.addAttribute("selectedSort", sort);

        return "user/category";
    }

    /**
     * Xác định cách sắp xếp
     */
    private Sort getSortOption(String sort) {
        return switch (sort) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "bestseller" -> Sort.by(Sort.Direction.DESC, "soLuongDaBan");
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "giaMin");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "giaMin");
            default -> Sort.by(Sort.Direction.DESC, "soLuongDaBan"); // popular
        };
    }

    /**
     * API để lọc sản phẩm động (AJAX)
     */
    @GetMapping("/{categoryId}/filter")
    @ResponseBody
    public Map<String, Object> filterProducts(
            @PathVariable("categoryId") Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "popular") String sort,
            @RequestParam(required = false) List<Integer> brand,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) List<Integer> size_filter,
            @RequestParam(required = false) List<Integer> color,
            @RequestParam(required = false) List<Integer> material,
            @RequestParam(required = false) Integer rating
    ) {
        Sort sorting = getSortOption(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<?> productsPage = productService.filterProducts(
                categoryId, brand, priceRange, size_filter,
                color, material, rating, pageable
        );

        return Map.of(
                "products", productsPage.getContent(),
                "currentPage", page,
                "totalPages", productsPage.getTotalPages(),
                "totalElements", productsPage.getTotalElements()
        );
    }
}