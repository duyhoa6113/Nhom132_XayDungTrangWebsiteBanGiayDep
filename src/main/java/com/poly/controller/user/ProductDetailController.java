package com.poly.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.entity.*;
import com.poly.repository.SanPhamChiTietRepository;
import com.poly.repository.SanPhamRepository;
import com.poly.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.*;
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

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private CartService cartService;  // THÊM DÒNG NÀY

    /**
     * Hiển thị trang chi tiết sản phẩm
     * URL: /product/{id}
     */
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Integer id,
                                Model model,
                                HttpSession session) {

        // Kiểm tra sản phẩm tồn tại
        SanPham sanPham = sanPhamRepository.findById(id).orElse(null);
        if (sanPham == null || sanPham.getTrangThai() != 1) {
            return "redirect:/";
        }

        // Lấy thông tin khách hàng từ session (nếu có)
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        // ============================================
        // LẤY VARIANTS
        // ============================================

        // Lọc variant active
        List<SanPhamChiTiet> activeVariants = sanPham.getVariants().stream()
                .filter(v -> v.isActive())
                .sorted(Comparator.comparing(SanPhamChiTiet::getSoLuongTon).reversed())
                .collect(Collectors.toList());

        if (activeVariants.isEmpty()) {
            model.addAttribute("error", "Sản phẩm hiện không có phiên bản nào");
            return "user/product-detail";
        }

        // ============================================
        // LẤY MÀU SẮC VÀ KÍCH THƯỚC UNIQUE
        // ============================================

        Set<MauSac> uniqueColors = activeVariants.stream()
                .map(SanPhamChiTiet::getMauSac)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<KichThuoc> uniqueSizes = activeVariants.stream()
                .map(SanPhamChiTiet::getKichThuoc)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // ============================================
        // MAP HÌNH ẢNH THEO MÀU
        // ============================================

        Map<Integer, List<String>> imagesByColor = new HashMap<>();

        for (SanPhamChiTiet v : activeVariants) {
            if (v.getMauSac() != null && v.getHinhAnh() != null && !v.getHinhAnh().isEmpty()) {
                imagesByColor.computeIfAbsent(v.getMauSac().getMauSacId(), k -> new ArrayList<>())
                        .add(v.getHinhAnh());
            }
        }

        // Nếu không có ảnh variant nào, dùng ảnh chính của sản phẩm
        if (imagesByColor.isEmpty() && sanPham.getHinhAnhChinh() != null) {
            for (MauSac color : uniqueColors) {
                imagesByColor.put(color.getMauSacId(),
                        Collections.singletonList(sanPham.getHinhAnhChinh()));
            }
        }

        // Nếu vẫn không có ảnh nào, dùng placeholder
        if (imagesByColor.isEmpty()) {
            String placeholderImage = "/images/placeholder-product.png";
            for (MauSac color : uniqueColors) {
                imagesByColor.put(color.getMauSacId(),
                        Collections.singletonList(placeholderImage));
            }
        }

        // ============================================
        // CHUẨN BỊ DỮ LIỆU CHO JAVASCRIPT
        // ============================================

        List<Map<String, Object>> variantsDataForJs = activeVariants.stream()
                .map(v -> {
                    Map<String, Object> variantMap = new HashMap<>();
                    variantMap.put("variantId", v.getVariantId());
                    variantMap.put("colorId", v.getMauSac().getMauSacId());
                    variantMap.put("sizeId", v.getKichThuoc().getKichThuocId());
                    variantMap.put("stock", v.getSoLuongTon()); // ✅ TỒN KHO
                    variantMap.put("price", v.getGiaBan());
                    variantMap.put("sku", v.getSKU());
                    return variantMap;
                })
                .collect(Collectors.toList());



        // ============================================
        // TÍNH TOÁN GIÁ
        // ============================================

        // Giá thấp nhất và cao nhất
        BigDecimal minPrice = activeVariants.stream()
                .map(SanPhamChiTiet::getGiaBan)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxPrice = activeVariants.stream()
                .map(SanPhamChiTiet::getGiaBan)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // Giá gốc cao nhất (để hiển thị giảm giá)
        BigDecimal maxOriginalPrice = activeVariants.stream()
                .map(SanPhamChiTiet::getGiaGoc)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(null);

        // Tính % giảm giá lớn nhất
        Integer maxDiscountPercent = activeVariants.stream()
                .map(SanPhamChiTiet::getTyLeGiamGia)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);

        // ============================================
        // TỔNG TỒN KHO
        // ============================================

        Integer totalStock = activeVariants.stream()
                .map(SanPhamChiTiet::getSoLuongTon)
                .reduce(0, Integer::sum);

        // ============================================
        // SẢN PHẨM LIÊN QUAN
        // ============================================

        List<SanPham> relatedProducts = new ArrayList<>();
        if (sanPham.getDanhMuc() != null) {
            relatedProducts = sanPhamRepository
                    .findByDanhMuc_DanhMucIdAndSanPhamIdNotAndTrangThai(
                            sanPham.getDanhMuc().getDanhMucId(),
                            id,
                            1,
                            PageRequest.of(0, 6)
                    ).getContent();
        }

        // Nếu không đủ sản phẩm liên quan, lấy thêm sản phẩm khác
        if (relatedProducts.size() < 6) {
            List<SanPham> moreProducts = sanPhamRepository
                    .findBySanPhamIdNotAndTrangThai(id, 1, PageRequest.of(0, 6 - relatedProducts.size()))
                    .getContent();
            relatedProducts.addAll(moreProducts);
        }


        // ========== THÊM CART COUNT - QUAN TRỌNG ==========
        try {
            if (khachHang != null) {
                Integer cartCount = cartService.getCartCount(khachHang);
                model.addAttribute("cartCount", cartCount != null ? cartCount : 0);
            } else {
                model.addAttribute("cartCount", 0);
            }
        } catch (Exception e) {
            model.addAttribute("cartCount", 0);
        }
        // ===================================================

        // ============================================
        // THÊM DỮ LIỆU VÀO MODEL
        // ============================================

        model.addAttribute("product", sanPham);
        model.addAttribute("uniqueColors", uniqueColors);
        model.addAttribute("uniqueSizes", uniqueSizes);
        model.addAttribute("activeVariants", activeVariants);
        model.addAttribute("imagesByColor", imagesByColor);
        model.addAttribute("variantsData", variantsDataForJs);
        model.addAttribute("relatedProducts", relatedProducts);

        // Giá và giảm giá
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("maxOriginalPrice", maxOriginalPrice);
        model.addAttribute("maxDiscountPercent", maxDiscountPercent);

        // Tồn kho
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("hasStock", totalStock > 0);

        // Metadata
        model.addAttribute("pageTitle", sanPham.getTen() + " - NiceSport");
        model.addAttribute("pageDescription",
                sanPham.getMoTa() != null && !sanPham.getMoTa().isEmpty()
                        ? sanPham.getMoTa().substring(0, Math.min(150, sanPham.getMoTa().length()))
                        : sanPham.getTen());

        // Convert imagesByColor và variantsData sang JSON để truyền cho JavaScript
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            model.addAttribute("imagesByColorJson", objectMapper.writeValueAsString(imagesByColor));
            model.addAttribute("variantsDataJson", objectMapper.writeValueAsString(variantsDataForJs));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("imagesByColorJson", "{}");
            model.addAttribute("variantsDataJson", "[]");
        }

        return "user/product-detail";
    }
}