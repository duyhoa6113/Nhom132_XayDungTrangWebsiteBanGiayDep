package com.poly.controller.admin;

import com.poly.entity.ChatLieu;
import com.poly.repository.ChatLieuRepository;
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
 * Controller quản lý Chất liệu
 */
@Controller
@RequestMapping("/admin/products/materials")
@RequiredArgsConstructor
@Slf4j
public class MaterialsManagerController {

    private final ChatLieuRepository chatLieuRepository;

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(value = "trangThai", required = false) Integer trangThai,
            Model model) {

        try {
            List<ChatLieu> materials;
            if (trangThai != null) {
                materials = chatLieuRepository.findByTrangThaiOrderByTenAsc(trangThai);
            } else {
                materials = chatLieuRepository.findAll();
            }

            long totalCount = chatLieuRepository.count();
            long activeCount = chatLieuRepository.findByTrangThaiOrderByTenAsc(1).size();

            model.addAttribute("materials", materials);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("activeCount", activeCount);
            model.addAttribute("selectedTrangThai", trangThai);
            model.addAttribute("page", "product_attributes");
            model.addAttribute("subpage", "materials");

        } catch (Exception e) {
            log.error("Error loading materials: {}", e.getMessage(), e);
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("materials", List.of());
            model.addAttribute("totalCount", 0L);
            model.addAttribute("activeCount", 0L);
        }

        return "admin/products/materials/index";
    }

    @PostMapping("/add")
    public String add(
            @RequestParam("ten") String ten,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        try {
            var existing = chatLieuRepository.findAll().stream()
                    .filter(m -> m.getTen().equalsIgnoreCase(ten))
                    .findFirst();
            if (existing.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Tên chất liệu đã tồn tại");
                return "redirect:/admin/products/materials";
            }

            ChatLieu material = new ChatLieu();
            material.setTen(ten);
            material.setTrangThai(trangThai != null ? trangThai : 1);

            chatLieuRepository.save(material);
            redirectAttributes.addFlashAttribute("success", "✅ Thêm chất liệu thành công!");

        } catch (Exception e) {
            log.error("❌ Lỗi khi thêm chất liệu: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/products/materials";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Integer id,
            @RequestParam("ten") String ten,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        try {
            ChatLieu material = chatLieuRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chất liệu với ID: " + id));

            var existing = chatLieuRepository.findAll().stream()
                    .filter(m -> m.getTen().equalsIgnoreCase(ten) && !m.getChatLieuId().equals(id))
                    .findFirst();
            if (existing.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Tên chất liệu đã tồn tại");
                return "redirect:/admin/products/materials";
            }

            material.setTen(ten);
            material.setTrangThai(trangThai);
            chatLieuRepository.save(material);
            redirectAttributes.addFlashAttribute("success", "✅ Cập nhật chất liệu thành công!");

        } catch (Exception e) {
            log.error("❌ Lỗi khi cập nhật chất liệu: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/products/materials";
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ChatLieu material = chatLieuRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chất liệu với ID: " + id));
            material.setTrangThai(0);
            chatLieuRepository.save(material);
            response.put("success", true);
            response.put("message", "Xóa chất liệu thành công");
        } catch (Exception e) {
            log.error("❌ Lỗi khi xóa chất liệu: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * API: Lấy chất liệu theo ID (JSON)
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            ChatLieu material = chatLieuRepository.findById(id)
                    .orElse(null);

            if (material != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("chatLieuId", material.getChatLieuId());
                data.put("ten", material.getTen());
                data.put("trangThai", material.getTrangThai());
                response.put("success", true);
                response.put("data", data);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy chất liệu");
            }

        } catch (Exception e) {
            log.error("❌ Lỗi khi lấy chất liệu: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}

