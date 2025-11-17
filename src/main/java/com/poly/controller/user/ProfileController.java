package com.poly.controller.user;

import com.poly.dto.ProfileUpdateRequest;
import com.poly.entity.DiaChi;
import com.poly.entity.KhachHang;
import com.poly.repository.KhachHangRepository;
import com.poly.service.CartService;
import com.poly.service.DiaChiService;
import com.poly.service.KhachHangService;
import com.poly.service.OtpService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProfileController - Quản lý trang cá nhân
 */
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final KhachHangService khachHangService;
    private final DiaChiService diaChiService;
    private final CartService cartService;
    private final OtpService  otpService;
    private final KhachHangRepository khachHangRepository;

    /**
     * Hiển thị trang hồ sơ cá nhân
     */
    @GetMapping
    public String viewProfile(HttpSession session, Model model) {
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        if (khachHang == null) {
            return "redirect:/login";
        }

        // Lấy thông tin mới nhất từ database
        khachHang = khachHangService.findById(khachHang.getKhachHangId());

        // Lấy danh sách địa chỉ
        List<DiaChi> addresses = diaChiService.getAddressesByKhachHangId(khachHang.getKhachHangId());

        model.addAttribute("khachHang", khachHang);
        model.addAttribute("addresses", addresses);
        model.addAttribute("activeTab", "profile");

        // ========== THÊM CART COUNT - QUAN TRỌNG ==========
        try {
            Integer cartCount = cartService.getCartCount(khachHang);
            model.addAttribute("cartCount", cartCount != null ? cartCount : 0);
        } catch (Exception e) {
            log.error("Lỗi khi lấy cart count", e);
            model.addAttribute("cartCount", 0);
        }
        // ===================================================

        return "user/profile";
    }

    /**
     * Cập nhật thông tin cá nhân - API
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            HttpSession session) {

        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        if (khachHang == null) {
            return ResponseEntity.ok(createResponse(false, "Vui lòng đăng nhập"));
        }

        try {
            KhachHang updated = khachHangService.updateProfile(khachHang.getKhachHangId(), request);

            // Cập nhật session
            session.setAttribute("khachHang", updated);
            session.setAttribute("khachHangTen", updated.getHoTen());

            return ResponseEntity.ok(createResponse(true, "Cập nhật thông tin thành công"));

        } catch (Exception e) {
            log.error("Lỗi khi cập nhật profile", e);
            return ResponseEntity.ok(createResponse(false, e.getMessage()));
        }
    }

    /**
     * Đổi mật khẩu - API
     */
    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session) {

        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        if (khachHang == null) {
            return ResponseEntity.ok(createResponse(false, "Vui lòng đăng nhập"));
        }

        try {
            khachHangService.changePassword(
                    khachHang.getKhachHangId(),
                    currentPassword,
                    newPassword,
                    confirmPassword
            );

            return ResponseEntity.ok(createResponse(true, "Đổi mật khẩu thành công"));

        } catch (Exception e) {
            log.error("Lỗi khi đổi mật khẩu", e);
            return ResponseEntity.ok(createResponse(false, e.getMessage()));
        }
    }

    /**
     * Upload avatar - API
     */
    @PostMapping("/upload-avatar")
    @ResponseBody
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            HttpSession session) {

        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        if (khachHang == null) {
            return ResponseEntity.ok(createResponse(false, "Vui lòng đăng nhập"));
        }

        try {
            String avatarUrl = khachHangService.uploadAvatar(khachHang.getKhachHangId(), file);

            // Cập nhật session
            khachHang.setAvatar(avatarUrl);
            session.setAttribute("khachHang", khachHang);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật ảnh đại diện thành công");
            response.put("avatarUrl", avatarUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Lỗi khi upload avatar", e);
            return ResponseEntity.ok(createResponse(false, e.getMessage()));
        }
    }

    /**
     * Helper method tạo response
     */
    private Map<String, Object> createResponse(boolean success, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        return response;
    }

    /**
     * Gửi OTP để thay đổi email
     */
    @PostMapping("/send-otp-email")
    @ResponseBody
    public ResponseEntity<?> sendOtpForEmailChange(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        if (khachHang == null) {
            return ResponseEntity.ok(createResponse(false, "Vui lòng đăng nhập"));
        }

        try {
            String newEmail = request.get("newEmail");

            // Validate email
            if (newEmail == null || newEmail.trim().isEmpty()) {
                return ResponseEntity.ok(createResponse(false, "Email không được để trống"));
            }

            // Kiểm tra email đã tồn tại chưa
            if (khachHangRepository.existsByEmail(newEmail)) {
                return ResponseEntity.ok(createResponse(false, "Email đã được sử dụng"));
            }

            // Gửi OTP
            otpService.sendOtpForEmailChange(khachHang.getKhachHangId(), newEmail, session);

            return ResponseEntity.ok(createResponse(true, "Mã OTP đã được gửi tới email"));

        } catch (Exception e) {
            log.error("Lỗi khi gửi OTP", e);
            return ResponseEntity.ok(createResponse(false, e.getMessage()));
        }
    }

    /**
     * Xác thực OTP và thay đổi email
     */
    @PostMapping("/verify-and-change-email")
    @ResponseBody
    public ResponseEntity<?> verifyAndChangeEmail(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        if (khachHang == null) {
            return ResponseEntity.ok(createResponse(false, "Vui lòng đăng nhập"));
        }

        try {
            String newEmail = request.get("newEmail");
            String otpCode = request.get("otpCode");

            // Verify OTP
            if (!otpService.verifyOtp(newEmail, otpCode, session)) {
                return ResponseEntity.ok(createResponse(false, "Mã OTP không đúng hoặc đã hết hạn"));
            }

            // Thay đổi email
            khachHang = khachHangService.findById(khachHang.getKhachHangId());
            khachHang.setEmail(newEmail);
            khachHang.setUpdatedAt(LocalDateTime.now());
            khachHangRepository.save(khachHang);

            // Cập nhật session
            khachHang = khachHangService.findById(khachHang.getKhachHangId());
            session.setAttribute("khachHang", khachHang);


            log.info("Thay đổi email thành công cho khách hàng: {}", khachHang.getKhachHangId());

            return ResponseEntity.ok(createResponse(true, "Thay đổi email thành công"));

        } catch (Exception e) {
            log.error("Lỗi khi verify OTP và thay đổi email", e);
            return ResponseEntity.ok(createResponse(false, "Có lỗi xảy ra"));
        }
    }

    /**
     * Thay đổi số điện thoại - API
     */
    @PostMapping("/change-phone")
    @ResponseBody
    public ResponseEntity<?> changePhone(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        if (khachHang == null) {
            return ResponseEntity.ok(createResponse(false, "Vui lòng đăng nhập"));
        }

        try {
            String newPhone = request.get("newPhone");
            String password = request.get("password");

            log.info("Thay đổi số điện thoại cho khách hàng: {}", khachHang.getKhachHangId());

            khachHangService.changePhone(khachHang.getKhachHangId(), newPhone, password);

            // Cập nhật session
            khachHang = khachHangService.findById(khachHang.getKhachHangId());
            session.setAttribute("khachHang", khachHang);

            return ResponseEntity.ok(createResponse(true, "Thay đổi số điện thoại thành công"));

        } catch (Exception e) {
            log.error("Lỗi khi thay đổi số điện thoại", e);
            return ResponseEntity.ok(createResponse(false, e.getMessage()));
        }
    }
}