package com.poly.controller.admin;

import com.poly.entity.KichThuoc;
import com.poly.repository.KichThuocRepository;
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
 * Controller quản lý Kích thước
 */
@Controller
@RequestMapping("/admin/products/sizes")
@RequiredArgsConstructor
@Slf4j
public class SizesManagerController {

    private final KichThuocRepository kichThuocRepository;

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(value = "trangThai", required = false) Integer trangThai,
            Model model) {

        try {
            List<KichThuoc> sizes;
            if (trangThai != null) {
                sizes = kichThuocRepository.findByTrangThaiOrderByTenAsc(trangThai);
            } else {
                sizes = kichThuocRepository.findAll();
            }

            long totalCount = kichThuocRepository.count();
            long activeCount = kichThuocRepository.findByTrangThaiOrderByTenAsc(1).size();

            model.addAttribute("sizes", sizes);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("activeCount", activeCount);
            model.addAttribute("selectedTrangThai", trangThai);
            model.addAttribute("page", "product_attributes");
            model.addAttribute("subpage", "sizes");

        } catch (Exception e) {
            log.error("Error loading sizes: {}", e.getMessage(), e);
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("sizes", List.of());
            model.addAttribute("totalCount", 0L);
            model.addAttribute("activeCount", 0L);
        }

        return "admin/products/sizes/index";
    }

    @PostMapping("/add")
    public String add(
            @RequestParam("ten") String ten,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        try {
            var existing = kichThuocRepository.findAll().stream()
                    .filter(s -> s.getTen().equalsIgnoreCase(ten))
                    .findFirst();
            if (existing.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Tên kích thước đã tồn tại");
                return "redirect:/admin/products/sizes";
            }

            KichThuoc size = new KichThuoc();
            size.setTen(ten);
            size.setTrangThai(trangThai != null ? trangThai.byteValue() : (byte) 1);

            kichThuocRepository.save(size);
            redirectAttributes.addFlashAttribute("success", "✅ Thêm kích thước thành công!");

        } catch (Exception e) {
            log.error("❌ Lỗi khi thêm kích thước: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/products/sizes";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Integer id,
            @RequestParam("ten") String ten,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        try {
            KichThuoc size = kichThuocRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kích thước với ID: " + id));

            var existing = kichThuocRepository.findAll().stream()
                    .filter(s -> s.getTen().equalsIgnoreCase(ten) && !s.getKichThuocId().equals(id))
                    .findFirst();
            if (existing.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Tên kích thước đã tồn tại");
                return "redirect:/admin/products/sizes";
            }

            size.setTen(ten);
            size.setTrangThai(trangThai.byteValue());
            kichThuocRepository.save(size);
            redirectAttributes.addFlashAttribute("success", "✅ Cập nhật kích thước thành công!");

        } catch (Exception e) {
            log.error("❌ Lỗi khi cập nhật kích thước: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/products/sizes";
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            KichThuoc size = kichThuocRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kích thước với ID: " + id));
            size.setTrangThai((byte) 0);
            kichThuocRepository.save(size);
            response.put("success", true);
            response.put("message", "Xóa kích thước thành công");
        } catch (Exception e) {
            log.error("❌ Lỗi khi xóa kích thước: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * API: Lấy kích thước theo ID (JSON)
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            KichThuoc size = kichThuocRepository.findById(id)
                    .orElse(null);

            if (size != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("kichThuocId", size.getKichThuocId());
                data.put("ten", size.getTen());
                data.put("trangThai", size.getTrangThai());
                response.put("success", true);
                response.put("data", data);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy kích thước");
            }

        } catch (Exception e) {
            log.error("❌ Lỗi khi lấy kích thước: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}

