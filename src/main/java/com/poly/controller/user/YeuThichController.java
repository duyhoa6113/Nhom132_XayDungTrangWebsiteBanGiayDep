package com.poly.controller.user;

import com.poly.entity.KhachHang;
import com.poly.entity.SanPham;
import com.poly.service.YeuThichService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wishlist")
@Slf4j
public class YeuThichController {

    private final YeuThichService yeuThichService;

    /**
     * Hiển thị trang danh sách yêu thích
     */
    @GetMapping
    public String hienThiDanhSachYeuThich(HttpSession session, Model model) {
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        if (khachHang == null) {
            return "redirect:/login?returnUrl=/wishlist";
        }

        try {
            List<SanPham> danhSachYeuThich = yeuThichService.layDanhSachYeuThich(khachHang);
            long soLuong = yeuThichService.demSoLuongYeuThich(khachHang);

            model.addAttribute("danhSachYeuThich", danhSachYeuThich);
            model.addAttribute("soLuong", soLuong);

            log.info("Hiển thị danh sách yêu thích cho khách hàng: {}, số lượng: {}",
                    khachHang.getKhachHangId(), soLuong);

            return "wishlist";
        } catch (Exception e) {
            log.error("Lỗi khi hiển thị danh sách yêu thích: ", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách yêu thích");
            return "error";
        }
    }

    /**
     * Thêm sản phẩm vào yêu thích (AJAX)
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> themYeuThich(
            @RequestParam Integer sanPhamId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập để sử dụng tính năng này");
                response.put("requireLogin", true);
                return ResponseEntity.ok(response);
            }

            boolean success = yeuThichService.themYeuThich(khachHang, sanPhamId);

            if (success) {
                long soLuong = yeuThichService.demSoLuongYeuThich(khachHang);
                response.put("success", true);
                response.put("message", "Đã thêm vào danh sách yêu thích");
                response.put("count", soLuong);
                log.info("Khách hàng {} đã thêm sản phẩm {} vào yêu thích",
                        khachHang.getKhachHangId(), sanPhamId);
            } else {
                response.put("success", false);
                response.put("message", "Sản phẩm đã có trong danh sách yêu thích");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi thêm vào yêu thích: ", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Xóa sản phẩm khỏi yêu thích (AJAX)
     */
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> xoaYeuThich(
            @RequestParam Integer sanPhamId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập");
                response.put("requireLogin", true);
                return ResponseEntity.ok(response);
            }

            boolean success = yeuThichService.xoaYeuThich(khachHang, sanPhamId);

            if (success) {
                long soLuong = yeuThichService.demSoLuongYeuThich(khachHang);
                response.put("success", true);
                response.put("message", "Đã xóa khỏi danh sách yêu thích");
                response.put("count", soLuong);
                log.info("Khách hàng {} đã xóa sản phẩm {} khỏi yêu thích",
                        khachHang.getKhachHangId(), sanPhamId);
            } else {
                response.put("success", false);
                response.put("message", "Có lỗi xảy ra");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi xóa khỏi yêu thích: ", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Toggle yêu thích (AJAX)
     */
    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleYeuThich(
            @RequestParam Integer sanPhamId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập để sử dụng tính năng này");
                response.put("requireLogin", true);
                return ResponseEntity.ok(response);
            }

            boolean isAdded = yeuThichService.toggleYeuThich(khachHang, sanPhamId);
            long soLuong = yeuThichService.demSoLuongYeuThich(khachHang);

            response.put("success", true);
            response.put("isAdded", isAdded);
            response.put("message", isAdded ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích");
            response.put("count", soLuong);

            log.info("Khách hàng {} toggle sản phẩm {}: {}",
                    khachHang.getKhachHangId(), sanPhamId, isAdded ? "Thêm" : "Xóa");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi toggle yêu thích: ", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Kiểm tra trạng thái yêu thích (AJAX)
     */
    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> kiemTraYeuThich(
            @RequestParam Integer sanPhamId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                response.put("isWishlisted", false);
                return ResponseEntity.ok(response);
            }

            boolean isWishlisted = yeuThichService.kiemTraYeuThich(khachHang, sanPhamId);
            response.put("isWishlisted", isWishlisted);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra yêu thích: ", e);
            response.put("isWishlisted", false);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Lấy số lượng sản phẩm yêu thích (AJAX)
     */
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> demSoLuong(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                response.put("count", 0);
            } else {
                long soLuong = yeuThichService.demSoLuongYeuThich(khachHang);
                response.put("count", soLuong);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi đếm số lượng yêu thích: ", e);
            response.put("count", 0);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Lấy danh sách ID sản phẩm yêu thích (AJAX)
     */
    @GetMapping("/ids")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> layDanhSachId(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                response.put("ids", List.of());
            } else {
                List<Integer> ids = yeuThichService.layDanhSachIdYeuThich(khachHang.getKhachHangId());
                response.put("ids", ids);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách ID yêu thích: ", e);
            response.put("ids", List.of());
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Xóa tất cả sản phẩm yêu thích
     */
    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> xoaTatCa(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập");
                response.put("requireLogin", true);
                return ResponseEntity.ok(response);
            }

            boolean success = yeuThichService.xoaTatCaYeuThich(khachHang);

            if (success) {
                response.put("success", true);
                response.put("message", "Đã xóa tất cả sản phẩm yêu thích");
                response.put("count", 0);
                log.info("Khách hàng {} đã xóa tất cả sản phẩm yêu thích",
                        khachHang.getKhachHangId());
            } else {
                response.put("success", false);
                response.put("message", "Có lỗi xảy ra khi xóa");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi xóa tất cả yêu thích: ", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
