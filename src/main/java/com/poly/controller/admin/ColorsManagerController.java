package com.poly.controller.admin;

import com.poly.entity.MauSac;
import com.poly.repository.MauSacRepository;
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
 * Controller quản lý Màu sắc
 */
@Controller
@RequestMapping("/admin/products/colors")
@RequiredArgsConstructor
@Slf4j
public class ColorsManagerController {

    private final MauSacRepository mauSacRepository;

    /**
     * Hiển thị trang quản lý màu sắc
     */
    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(value = "trangThai", required = false) Integer trangThai,
            Model model) {

        try {
            List<MauSac> colors;
            if (trangThai != null) {
                colors = mauSacRepository.findByTrangThaiOrderByTenAsc(trangThai.byteValue());
            } else {
                colors = mauSacRepository.findAll();
            }

            long totalCount = mauSacRepository.count();
            long activeCount = mauSacRepository.findByTrangThaiOrderByTenAsc(1).size();

            model.addAttribute("colors", colors);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("activeCount", activeCount);
            model.addAttribute("selectedTrangThai", trangThai);
            model.addAttribute("page", "product_attributes");
            model.addAttribute("subpage", "colors");

        } catch (Exception e) {
            log.error("Error loading colors: {}", e.getMessage(), e);
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("colors", List.of());
            model.addAttribute("totalCount", 0L);
            model.addAttribute("activeCount", 0L);
        }

        return "admin/products/colors/index";
    }

    /**
     * Thêm mới màu sắc
     */
    @PostMapping("/add")
    public String add(
            @RequestParam("ten") String ten,
            @RequestParam(value = "maHex", required = false) String maHex,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        try {
            // Kiểm tra tên đã tồn tại chưa
            var existingByName = mauSacRepository.findAll().stream()
                    .filter(c -> c.getTen().equalsIgnoreCase(ten))
                    .findFirst();
            if (existingByName.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Tên màu sắc đã tồn tại");
                return "redirect:/admin/products/colors";
            }

            MauSac color = new MauSac();
            color.setTen(ten);
            color.setMaHex(maHex);
            color.setTrangThai(trangThai != null ? trangThai.byteValue() : (byte) 1);

            mauSacRepository.save(color);
            log.info("✅ Đã thêm màu sắc ID: {}", color.getMauSacId());

            redirectAttributes.addFlashAttribute("success", "✅ Thêm màu sắc thành công!");

        } catch (Exception e) {
            log.error("❌ Lỗi khi thêm màu sắc: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/products/colors";
    }

    /**
     * Cập nhật màu sắc
     */
    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Integer id,
            @RequestParam("ten") String ten,
            @RequestParam(value = "maHex", required = false) String maHex,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        try {
            MauSac color = mauSacRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy màu sắc với ID: " + id));

            // Kiểm tra tên đã tồn tại chưa (trừ chính nó)
            var existingByName = mauSacRepository.findAll().stream()
                    .filter(c -> c.getTen().equalsIgnoreCase(ten) && !c.getMauSacId().equals(id))
                    .findFirst();
            if (existingByName.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Tên màu sắc đã tồn tại");
                return "redirect:/admin/products/colors";
            }

            color.setTen(ten);
            color.setMaHex(maHex);
            color.setTrangThai(trangThai.byteValue());

            mauSacRepository.save(color);
            log.info("✅ Đã cập nhật màu sắc ID: {}", id);

            redirectAttributes.addFlashAttribute("success", "✅ Cập nhật màu sắc thành công!");

        } catch (Exception e) {
            log.error("❌ Lỗi khi cập nhật màu sắc: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/products/colors";
    }

    /**
     * Xóa màu sắc (soft delete)
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            MauSac color = mauSacRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy màu sắc với ID: " + id));

            // Soft delete
            color.setTrangThai((byte) 0);
            mauSacRepository.save(color);

            response.put("success", true);
            response.put("message", "Xóa màu sắc thành công");

        } catch (Exception e) {
            log.error("❌ Lỗi khi xóa màu sắc: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * API: Lấy màu sắc theo ID (JSON)
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            MauSac color = mauSacRepository.findById(id)
                    .orElse(null);

            if (color != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("mauSacId", color.getMauSacId());
                data.put("ten", color.getTen());
                data.put("maHex", color.getMaHex());
                data.put("trangThai", color.getTrangThai());
                response.put("success", true);
                response.put("data", data);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy màu sắc");
            }

        } catch (Exception e) {
            log.error("❌ Lỗi khi lấy màu sắc: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

}

