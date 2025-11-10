package com.poly.controller.user;

import com.poly.entity.SanPham;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

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
            Model model
    ) {
        try {
            // 1. Lấy thông tin danh mục
            var category = categoryService.getCategoryById(categoryId);
            if (category == null) return "redirect:/";
            model.addAttribute("category", category);

            // 2. Lấy tất cả danh mục cho sidebar
            var allCategories = categoryService.getAllActiveCategories();
            model.addAttribute("allCategories", allCategories);

            // 3. Lấy danh sách thương hiệu
            var brands = categoryService.getBrandsByCategory(categoryId);
            model.addAttribute("brands", brands);

            // 4. Lấy danh sách size
            var sizes = categoryService.getAllActiveSizes();
            model.addAttribute("sizes", sizes);

            // 5. Lấy danh sách màu sắc
            var colors = categoryService.getAllActiveColors();
            model.addAttribute("colors", colors);

            // 6. Lấy danh sách chất liệu
            var materials = categoryService.getMaterialsByCategory(categoryId);
            model.addAttribute("materials", materials);

            // 7. Xử lý sort
            Sort dbSort = Sort.by(Sort.Direction.DESC, "createdAt"); // mặc định
            boolean sortByPrice = false;
            boolean priceAsc = true;

            switch (sort) {
                case "newest":
                    dbSort = Sort.by(Sort.Direction.DESC, "createdAt");
                    break;
                case "bestseller":
                    dbSort = Sort.by(Sort.Direction.DESC, "soLuongDaBan");
                    break;
                case "price-asc":
                    sortByPrice = true;
                    priceAsc = true;
                    break;
                case "price-desc":
                    sortByPrice = true;
                    priceAsc = false;
                    break;
            }

            Pageable pageable = PageRequest.of(page, size, dbSort);

            // 8. Lấy sản phẩm
            Page<SanPham> productsPage;
            boolean hasFilters = (brand != null && !brand.isEmpty()) ||
                    (priceRange != null && !priceRange.isEmpty()) ||
                    (size_filter != null && !size_filter.isEmpty()) ||
                    (color != null && !color.isEmpty()) ||
                    (material != null && !material.isEmpty());

            if (hasFilters) {
                productsPage = productService.filterProducts(
                        categoryId, brand, priceRange, size_filter,
                        color, material, null, pageable
                );
            } else {
                productsPage = productService.getProductsByCategory(categoryId, pageable);
            }

            // Tạo list mutable để sort
            List<SanPham> products = new ArrayList<>(productsPage.getContent());

             // 9. Sort theo giá nếu cần
            if (sortByPrice) {
                if (priceAsc) {
                    products.sort(Comparator.comparing(SanPham::getGiaMin));
                } else {
                    products.sort(Comparator.comparing(SanPham::getGiaMin).reversed());
                }
            }


            // 10. Add vào model
            long totalProducts = productService.countProductsByCategory(categoryId);
            model.addAttribute("products", products);
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productsPage.getTotalPages());
            model.addAttribute("totalElements", productsPage.getTotalElements());

            // 11. Giữ lại filter và sort đã chọn
            model.addAttribute("selectedBrands", brand);
            model.addAttribute("selectedPriceRange", priceRange);
            model.addAttribute("selectedSizes", size_filter);
            model.addAttribute("selectedColors", color);
            model.addAttribute("selectedMaterials", material);
            model.addAttribute("selectedSort", sort);

            return "user/category";

        } catch (Exception e) {
            System.err.println("❌ ERROR in CategoryController: " + e.getClass().getName());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Đã xảy ra lỗi khi tải danh mục: " + e.getMessage());
            return "error-page";
        }
    }
}
