package com.poly.controller.user;

import com.poly.entity.SanPham;
import com.poly.service.CategoryService;
import com.poly.service.SearchService;
import com.poly.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller xử lý tìm kiếm sản phẩm
 */
@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    /**
     * Trang tìm kiếm chính
     */
    @GetMapping
    public String search(
            @RequestParam(value = "q", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "popular") String sort,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) List<Integer> brand,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) Integer rating,
            Model model
    ) {
        // 1. Nếu không có keyword, redirect về trang chủ
        if (keyword == null || keyword.trim().isEmpty()) {
            return "redirect:/";
        }

        keyword = keyword.trim();
        model.addAttribute("keyword", keyword);

        // 2. Lấy tất cả danh mục và brands
        var categories = categoryService.getAllActiveCategories();
        model.addAttribute("categories", categories);

        var brands = searchService.getBrandsFromSearchResults(keyword);
        model.addAttribute("brands", brands);

        Pageable pageable = PageRequest.of(page, size);
        Page<SanPham> productsPage;

        // 3. Xử lý sort
        switch (sort) {
            case "newest":
                productsPage = searchService.searchProducts(keyword, category, brand, priceRange, rating,
                        PageRequest.of(page, size));
                break;

            case "price-asc":
                productsPage = searchService.searchProductsSortedByPrice(
                        keyword, category, brand, priceRange, rating, true, page, size
                );
                break;

            case "price-desc":
                productsPage = searchService.searchProductsSortedByPrice(
                        keyword, category, brand, priceRange, rating, false, page, size
                );
                break;

            default: // popular / newest / bestseller
                productsPage = searchService.searchProducts(
                        keyword, category, brand, priceRange, rating, pageable
                );
                break;
        }

        // 4. Add dữ liệu vào model
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("totalResults", productsPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());

        // 5. Giữ lại filter
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedBrands", brand);
        model.addAttribute("selectedPriceRange", priceRange);
        model.addAttribute("selectedRating", rating);
        model.addAttribute("selectedSort", sort);

        // 6. Gợi ý nếu không có kết quả
        if (productsPage.getTotalElements() == 0) {
            var suggestions = searchService.getSuggestedKeywords(keyword);
            model.addAttribute("suggestions", suggestions);
        }

        return "user/search";
    }

    /**
     * API AJAX search suggestions (autocomplete)
     */
    @GetMapping("/suggestions")
    @ResponseBody
    public List<String> getSearchSuggestions(@RequestParam("q") String keyword) {
        return searchService.getSearchSuggestions(keyword);
    }
}
