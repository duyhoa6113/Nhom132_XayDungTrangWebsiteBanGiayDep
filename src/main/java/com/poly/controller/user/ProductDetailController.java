package com.poly.controller.user;

import com.poly.entity.KichThuoc;
import com.poly.entity.MauSac;
import com.poly.entity.SanPham;
import com.poly.entity.SanPhamChiTiet;
import com.poly.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    /**
     * Hiển thị trang chi tiết sản phẩm
     * URL: /product/{id}
     */
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Integer id, Model model) {
        SanPham sanPham = sanPhamRepository.findById(id).orElse(null);
        if (sanPham == null) {
            return "redirect:/";
        }

        // Lọc variant còn hàng và active
        List<SanPhamChiTiet> activeVariants = sanPham.getVariants().stream()
                .filter(v -> v.isActive() && v.isInStock())
                .collect(Collectors.toList());

        // Lấy màu sắc và kích thước unique
        Set<MauSac> uniqueColors = activeVariants.stream()
                .map(SanPhamChiTiet::getMauSac)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<KichThuoc> uniqueSizes = activeVariants.stream()
                .map(SanPhamChiTiet::getKichThuoc)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Map hình ảnh theo màu
        Map<Integer, List<String>> imagesByColor = new HashMap<>();
        for (SanPhamChiTiet v : activeVariants) {
            if (v.getMauSac() != null && v.getHinhAnh() != null && !v.getHinhAnh().isEmpty()) {
                imagesByColor.computeIfAbsent(v.getMauSac().getMauSacId(), k -> new ArrayList<>())
                        .add(v.getHinhAnh());
            }
        }
        // Nếu không có ảnh nào, dùng ảnh chính
        if (imagesByColor.isEmpty() && sanPham.getHinhAnhChinh() != null) {
            for (MauSac color : uniqueColors) {
                imagesByColor.put(color.getMauSacId(),
                        Collections.singletonList(sanPham.getHinhAnhChinh()));
            }
        }

        // Chuẩn bị dữ liệu cho JS
        List<Map<String, ? extends Number>> variantsDataForJs = activeVariants.stream().map(v -> Map.of(
                "variantId", v.getVariantId(),
                "colorId", v.getMauSac().getMauSacId(),
                "sizeId", v.getKichThuoc().getKichThuocId(),
                "stock", v.getSoLuongTon(),
                "price", v.getGiaBan()
        )).collect(Collectors.toList());

        // Sản phẩm liên quan
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

        model.addAttribute("product", sanPham);
        model.addAttribute("uniqueColors", uniqueColors);
        model.addAttribute("uniqueSizes", uniqueSizes);
        model.addAttribute("activeVariants", activeVariants);
        model.addAttribute("imagesByColor", imagesByColor);
        model.addAttribute("variantsData", variantsDataForJs);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("pageTitle", sanPham.getTen());

        return "user/product-detail";
    }
}