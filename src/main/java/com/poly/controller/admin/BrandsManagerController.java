package com.poly.controller.admin;

import com.poly.entity.ThuongHieu;
import com.poly.repository.ThuongHieuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý Thương hiệu
 */
@Controller
@RequestMapping("/admin/products/brands")
@RequiredArgsConstructor
@Slf4j
public class BrandsManagerController {

    private final ThuongHieuRepository thuongHieuRepository;

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(value = "trangThai", required = false) Integer trangThai,
            Model model) {

        try {
            List<ThuongHieu> brands;
            if (trangThai != null) {
                brands = thuongHieuRepository.findByTrangThaiOrderByTenAsc(trangThai);
            } else {
                brands = thuongHieuRepository.findAll();
            }

            long totalCount = thuongHieuRepository.count();
            long activeCount = thuongHieuRepository.findByTrangThaiOrderByTenAsc(1).size();

            model.addAttribute("brands", brands);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("activeCount", activeCount);
            model.addAttribute("selectedTrangThai", trangThai);
            model.addAttribute("page", "product_attributes");
            model.addAttribute("subpage", "brands");

        } catch (Exception e) {
            log.error("Error loading brands: {}", e.getMessage(), e);
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("brands", List.of());
            model.addAttribute("totalCount", 0L);
            model.addAttribute("activeCount", 0L);
        }

        return "admin/products/brands/index";
    }

    @PostMapping("/add")
    public String add(
            @RequestParam("ten") String ten,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        try {
            var existing = thuongHieuRepository.findByTen(ten);
            if (existing.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Tên thương hiệu đã tồn tại");
                return "redirect:/admin/products/brands";
            }

            ThuongHieu brand = new ThuongHieu();
            brand.setTen(ten);
            brand.setMoTa(moTa);
            brand.setTrangThai(trangThai != null ? trangThai : 1);

            thuongHieuRepository.save(brand);
            redirectAttributes.addFlashAttribute("success", "✅ Thêm thương hiệu thành công!");

        } catch (Exception e) {
            log.error("❌ Lỗi khi thêm thương hiệu: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/products/brands";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Integer id,
            @RequestParam("ten") String ten,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        try {
            ThuongHieu brand = thuongHieuRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thương hiệu với ID: " + id));

            var existing = thuongHieuRepository.findByTen(ten);
            if (existing.isPresent() && !existing.get().getThuongHieuId().equals(id)) {
                redirectAttributes.addFlashAttribute("error", "Tên thương hiệu đã tồn tại");
                return "redirect:/admin/products/brands";
            }

            brand.setTen(ten);
            brand.setMoTa(moTa);
            brand.setTrangThai(trangThai);
            thuongHieuRepository.save(brand);
            redirectAttributes.addFlashAttribute("success", "✅ Cập nhật thương hiệu thành công!");

        } catch (Exception e) {
            log.error("❌ Lỗi khi cập nhật thương hiệu: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/products/brands";
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ThuongHieu brand = thuongHieuRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thương hiệu với ID: " + id));
            brand.setTrangThai(0);
            thuongHieuRepository.save(brand);
            response.put("success", true);
            response.put("message", "Xóa thương hiệu thành công");
        } catch (Exception e) {
            log.error("❌ Lỗi khi xóa thương hiệu: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * API: Lấy thương hiệu theo ID (JSON)
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            ThuongHieu brand = thuongHieuRepository.findById(id)
                    .orElse(null);

            if (brand != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("thuongHieuId", brand.getThuongHieuId());
                data.put("ten", brand.getTen());
                data.put("moTa", brand.getMoTa());
                data.put("trangThai", brand.getTrangThai());
                response.put("success", true);
                response.put("data", data);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy thương hiệu");
            }

        } catch (Exception e) {
            log.error("❌ Lỗi khi lấy thương hiệu: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}

