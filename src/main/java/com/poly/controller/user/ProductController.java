package com.poly.controller.user;

import com.poly.dto.ProductDetailDTO;
import com.poly.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductDetailService productDetailService;

    /**
     * Trang chi tiết sản phẩm
     * URL: /product/{id}
     */
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Integer id, Model model) {
        try {
            // Lấy thông tin chi tiết sản phẩm
            ProductDetailDTO product = productDetailService.getProductDetail(id);

            // Lấy danh sách màu sắc duy nhất từ các variants
            Set<String> uniqueColors = product.getVariants().stream()
                    .map(variant -> variant.getMauSacTen())
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            // Lấy danh sách size duy nhất từ các variants
            Set<String> uniqueSizes = product.getVariants().stream()
                    .map(variant -> variant.getKichThuocTen())
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            // Add vào model để hiển thị trên template
            model.addAttribute("product", product);
            model.addAttribute("uniqueColors", uniqueColors);
            model.addAttribute("uniqueSizes", uniqueSizes);

            // TODO: Có thể thêm sản phẩm liên quan
            // List<ProductHomeDTO> relatedProducts = productService.getRelatedProducts(id);
            // model.addAttribute("relatedProducts", relatedProducts);

            return "Product detail";

        } catch (Exception e) {
            // Log error
            e.printStackTrace();

            // Redirect về trang chủ hoặc trang lỗi
            model.addAttribute("errorMessage", "Không tìm thấy sản phẩm");
            return "redirect:/";
        }
    }
}