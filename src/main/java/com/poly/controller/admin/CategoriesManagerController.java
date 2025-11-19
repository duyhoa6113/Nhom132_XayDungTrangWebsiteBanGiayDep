package com.poly.controller.admin;

import com.poly.dto.DanhMucDTO;
import com.poly.service.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý danh mục - LOAD DATA TỪ DATABASE
 *
 * @author Nhóm 132
 */
@Controller
@RequestMapping("/admin/categories")
public class CategoriesManagerController {

    @Autowired
    private DanhMucService danhMucService;  // ✅ Service để load data từ DB

    /**
     * Hiển thị trang quản lý danh mục - LOAD TỪ DATABASE
     * URL: /admin/categories
     */
    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(value = "trangThai", required = false) Integer trangThai,
            Model model,
            HttpSession session) {

        System.out.println("========================================");
        System.out.println("🔍 DEBUG: Đang load danh mục từ database...");

        try {
            List<DanhMucDTO> danhMucs;

            // ✅ LOAD DATA TỪ DATABASE qua Service
            if (trangThai != null && trangThai == 1) {
                System.out.println("📊 Lấy danh mục HOẠT ĐỘNG từ DB...");
                danhMucs = danhMucService.getActiveDanhMuc();
            } else if (trangThai != null && trangThai == 0) {
                System.out.println("📊 Lấy danh mục KHÔNG HOẠT ĐỘNG từ DB...");
                danhMucs = danhMucService.getAllDanhMuc().stream()
                        .filter(dm -> dm.getTrangThai() != null && dm.getTrangThai() == 0)
                        .toList();
            } else {
                System.out.println("📊 Lấy TẤT CẢ danh mục từ DB...");
                danhMucs = danhMucService.getAllDanhMuc();
            }

            // Debug: In ra số lượng
            System.out.println("✅ Đã load " + danhMucs.size() + " danh mục từ database");
            if (!danhMucs.isEmpty()) {
                System.out.println("📝 Danh mục đầu tiên: " + danhMucs.get(0).getTen());
            }

            // ✅ Lấy thống kê từ database
            long totalCount = danhMucService.getTotalCount();
            long activeCount = danhMucService.getActiveCount();

            System.out.println("📊 Tổng: " + totalCount + " | Hoạt động: " + activeCount);

            // ✅ Add attributes vào Model để Thymeleaf hiển thị
            model.addAttribute("danhMucs", danhMucs);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("activeCount", activeCount);
            model.addAttribute("selectedTrangThai", trangThai);
            model.addAttribute("page", "categories");  // Để sidebar active

            System.out.println("✅ Đã add attributes vào Model");
            System.out.println("========================================");

            return "admin/categories/index";

        } catch (Exception e) {
            System.err.println("❌ LỖI khi load danh mục: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("danhMucs", List.of());  // Empty list
            model.addAttribute("totalCount", 0L);
            model.addAttribute("activeCount", 0L);

            return "admin/categories/index";
        }
    }

    /**
     * Thêm mới danh mục - LƯU VÀO DATABASE
     */
    @PostMapping("/add")
    public String add(
            @RequestParam("ten") String ten,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        System.out.println("➕ Đang thêm danh mục: " + ten);

        try {
            DanhMucDTO dto = new DanhMucDTO();
            dto.setTen(ten);
            dto.setMoTa(moTa);
            dto.setTrangThai(trangThai != null ? trangThai : 1);

            // ✅ LƯU VÀO DATABASE qua Service
            DanhMucDTO saved = danhMucService.createDanhMuc(dto);
            System.out.println("✅ Đã lưu danh mục ID: " + saved.getDanhMucId());

            redirectAttributes.addFlashAttribute("success", "✅ Thêm danh mục thành công!");

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi thêm: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    /**
     * Cập nhật danh mục - UPDATE DATABASE
     */
    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Integer id,
            @RequestParam("ten") String ten,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        System.out.println("📝 Đang cập nhật danh mục ID: " + id);

        try {
            DanhMucDTO dto = new DanhMucDTO();
            dto.setTen(ten);
            dto.setMoTa(moTa);
            dto.setTrangThai(trangThai);

            // ✅ UPDATE DATABASE qua Service
            danhMucService.updateDanhMuc(id, dto);
            System.out.println("✅ Đã cập nhật danh mục ID: " + id);

            redirectAttributes.addFlashAttribute("success", "✅ Cập nhật danh mục thành công!");

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi cập nhật: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    /**
     * Xóa danh mục - DELETE FROM DATABASE
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        System.out.println("🗑️ Đang xóa danh mục ID: " + id);

        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ DELETE FROM DATABASE qua Service
            boolean deleted = danhMucService.deleteDanhMuc(id);

            response.put("success", deleted);
            response.put("message", deleted ? "Xóa danh mục thành công" : "Không thể xóa");

            System.out.println(deleted ? "✅ Đã xóa" : "❌ Không xóa được");

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xóa: " + e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * API: Lấy danh sách (JSON)
     */
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getList(
            @RequestParam(required = false) Integer trangThai) {

        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ LOAD FROM DATABASE
            List<DanhMucDTO> categories = (trangThai != null && trangThai == 1)
                    ? danhMucService.getActiveDanhMuc()
                    : danhMucService.getAllDanhMuc();

            response.put("success", true);
            response.put("data", categories);
            response.put("total", categories.size());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * API: Lấy theo ID (JSON)
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ LOAD FROM DATABASE
            var category = danhMucService.getDanhMucById(id);

            if (category.isPresent()) {
                response.put("success", true);
                response.put("data", category.get());
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy");
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}