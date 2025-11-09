package com.poly.controller.user;

import com.poly.entity.KichThuoc;
import com.poly.entity.MauSac;
import com.poly.entity.SanPham;
import com.poly.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller cho trang chi tiết sản phẩm
 *
 * @author Nhóm 132
 */
@Controller
public class ProductDetailController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    /**
     * Hiển thị trang chi tiết sản phẩm
     * URL: /product/{id}
     */
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Integer id, Model model) {

        // Lấy thông tin sản phẩm theo ID
        SanPham sanPham = sanPhamRepository.findById(id).orElse(null);

        // Nếu không tìm thấy sản phẩm, redirect về trang chủ
        if (sanPham == null) {
            return "redirect:/";
        }

        // ============================================
        // LỌC MÀU SẮC VÀ KÍCH THƯỚC DUY NHẤT
        // ============================================

        // Lấy danh sách màu sắc unique (không trùng lặp)
        Set<MauSac> uniqueColors = sanPham.getVariants().stream()
                .filter(v -> v.getTrangThai() == 1)  // Chỉ lấy variant active
                .map(v -> v.getMauSac())              // Lấy màu sắc
                .collect(Collectors.toCollection(LinkedHashSet::new));  // Loại bỏ trùng lặp, giữ thứ tự

        // Lấy danh sách kích thước unique (không trùng lặp)
        Set<KichThuoc> uniqueSizes = sanPham.getVariants().stream()
                .filter(v -> v.getTrangThai() == 1)   // Chỉ lấy variant active
                .filter(v -> v.getSoLuongTon() > 0)   // Chỉ lấy còn hàng
                .map(v -> v.getKichThuoc())            // Lấy kích thước
                .collect(Collectors.toCollection(LinkedHashSet::new));  // Loại bỏ trùng lặp, giữ thứ tự

        // ============================================
        // LẤY SẢN PHẨM LIÊN QUAN (CÙNG DANH MỤC)
        // ============================================

        List<SanPham> relatedProducts = List.of(); // Khởi tạo list rỗng

        if (sanPham.getDanhMuc() != null) {
            // Lấy 4 sản phẩm cùng danh mục, loại trừ sản phẩm hiện tại
            relatedProducts = sanPhamRepository
                    .findByDanhMuc_DanhMucIdAndSanPhamIdNotAndTrangThai(
                            sanPham.getDanhMuc().getDanhMucId(),
                            id,
                            1,
                            PageRequest.of(0, 4)
                    ).getContent();

            // Nếu không đủ 4 sản phẩm cùng danh mục, lấy thêm sản phẩm khác
            if (relatedProducts.size() < 4) {
                int remaining = 4 - relatedProducts.size();
                List<SanPham> additionalProducts = sanPhamRepository
                        .findBySanPhamIdNotAndTrangThai(id, 1, PageRequest.of(0, remaining))
                        .getContent();

                // Merge 2 lists
                relatedProducts = new java.util.ArrayList<>(relatedProducts);
                relatedProducts.addAll(additionalProducts);
            }
        }

        // Đưa dữ liệu vào model
        model.addAttribute("product", sanPham);
        model.addAttribute("uniqueColors", uniqueColors);
        model.addAttribute("uniqueSizes", uniqueSizes);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("pageTitle", sanPham.getTen());

        return "user/product-detail";
    }
}