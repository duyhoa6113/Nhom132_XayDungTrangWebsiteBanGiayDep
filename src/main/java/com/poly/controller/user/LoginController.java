package com.poly.controller.user;

import com.poly.dto.ResetPasswordDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.dto.ForgotPasswordDTO;
import com.poly.dto.LoginDTO;
import com.poly.dto.RegisterDTO;
import com.poly.entity.KhachHang;
import com.poly.service.LoginService;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    // ============================================
    // ĐĂNG KÝ - REGISTER
    // ============================================

    /**
     * Hiển thị trang đăng ký
     * GET /register
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model, HttpSession session) {
        // Nếu đã đăng nhập, redirect về trang chủ
        if (session.getAttribute("khachHang") != null) {
            return "redirect:/Index";
        }

        model.addAttribute("registerDTO", new RegisterDTO());
        return "user/register";
    }

    /**
     * Xử lý đăng ký tài khoản mới
     * POST /register
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDTO registerDTO,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        try {
            // Đăng ký tài khoản
            KhachHang khachHang = loginService.register(registerDTO);

            // Thành công - redirect về trang login
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";

        } catch (RuntimeException e) {
            // Lỗi - hiển thị thông báo lỗi
            log.error("Lỗi khi đăng ký: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "user/register";
        }
    }

    // ============================================
    // ĐĂNG NHẬP - LOGIN
    // ============================================

    /**
     * Hiển thị trang đăng nhập
     * GET /login
     */
    @GetMapping("/login")
    public String showLoginPage(Model model, HttpSession session) {
        // Nếu đã đăng nhập, redirect về trang chủ
        if (session.getAttribute("khachHang") != null) {
            return "redirect:/Index";
        }

        model.addAttribute("loginDTO", new LoginDTO());
        return "user/login";
    }

    /**
     * Xử lý đăng nhập
     * POST /login
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model,
                        RedirectAttributes redirectAttributes) {

        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            return "user/login";
        }

        try {
            // Thực hiện đăng nhập
            Optional<KhachHang> khachHangOpt = loginService.login(loginDTO);

            if (khachHangOpt.isEmpty()) {
                // Đăng nhập thất bại
                model.addAttribute("errorMessage", "Email hoặc mật khẩu không chính xác");
                return "user/login";
            }

            // Đăng nhập thành công - lưu thông tin vào session
            KhachHang khachHang = khachHangOpt.get();
            session.setAttribute("khachHang", khachHang);
            session.setAttribute("khachHangId", khachHang.getKhachHangId());
            session.setAttribute("khachHangTen", khachHang.getHoTen());
            session.setAttribute("khachHangEmail", khachHang.getEmail());

            log.info("Khách hàng {} đăng nhập thành công", khachHang.getEmail());

            // Redirect về trang chủ hoặc trang trước đó
            String redirectUrl = (String) session.getAttribute("redirectUrl");
            if (redirectUrl != null) {
                session.removeAttribute("redirectUrl");
                return "redirect:" + redirectUrl;
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng nhập thành công! Chào mừng " + khachHang.getHoTen());
            return "redirect:/Index";

        } catch (Exception e) {
            log.error("Lỗi khi đăng nhập: {}", e.getMessage());
            model.addAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại!");
            return "user/login";
        }
    }

    // ============================================
    // ĐĂNG XUẤT - LOGOUT
    // ============================================

    /**
     * Đăng xuất
     * GET /logout
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Xóa thông tin khách hàng khỏi session
        String tenKhachHang = (String) session.getAttribute("khachHangTen");

        session.removeAttribute("khachHang");
        session.removeAttribute("khachHangId");
        session.removeAttribute("khachHangTen");
        session.removeAttribute("khachHangEmail");
        session.invalidate();

        log.info("Khách hàng {} đã đăng xuất", tenKhachHang);

        redirectAttributes.addFlashAttribute("successMessage", "Đã đăng xuất thành công");
        return "redirect:/Index";
    }

    // ============================================
    // QUÊN MẬT KHẨU - FORGOT PASSWORD
    // ============================================

    /**
     * Hiển thị trang quên mật khẩu
     * GET /forgot-password
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        log.info("Displaying forgot password page");
        model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO());
        return "user/forgot-password";
    }

    /**
     * Xử lý yêu cầu quên mật khẩu (gửi OTP qua email)
     * POST /forgot-password
     */
    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @ModelAttribute ForgotPasswordDTO forgotPasswordDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "user/forgot-password";
        }

        try {
            String email = forgotPasswordDTO.getEmail();

            // Kiểm tra email có tồn tại không
            if (!loginService.emailExists(email)) {
                model.addAttribute("errorMessage", "Email không tồn tại trong hệ thống");
                return "user/forgot-password";
            }

            // Tạo OTP 6 số ngẫu nhiên
            String otp = String.format("%06d", new java.util.Random().nextInt(999999));

            // Lưu OTP và email vào session (có hiệu lực 5 phút)
            session.setAttribute("resetPasswordOTP", otp);
            session.setAttribute("resetPasswordEmail", email);
            session.setAttribute("otpCreatedTime", System.currentTimeMillis());

            // Gửi OTP qua email
            loginService.sendOTPEmail(email, otp);

            log.info("✅ Đã gửi OTP đến email: {}", email);

            // Chuyển đến trang xác minh OTP
            return "redirect:/verify-otp";

        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi OTP: {}", e.getMessage());
            model.addAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại sau!");
            return "user/forgot-password";
        }
    }

    /**
     * Hiển thị trang xác minh OTP
     * GET /verify-otp
     */
    @GetMapping("/verify-otp")
    public String showVerifyOTPPage(HttpSession session, Model model) {
        // Kiểm tra có email trong session không
        if (session.getAttribute("resetPasswordEmail") == null) {
            return "redirect:/forgot-password";
        }

        model.addAttribute("email", session.getAttribute("resetPasswordEmail"));
        return "user/verify-otp";
    }

    /**
     * Xử lý xác minh OTP
     * POST /verify-otp
     */
    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam String otp,
                            HttpSession session,
                            Model model) {

        String sessionOTP = (String) session.getAttribute("resetPasswordOTP");
        String email = (String) session.getAttribute("resetPasswordEmail");
        Long otpCreatedTime = (Long) session.getAttribute("otpCreatedTime");

        // Kiểm tra OTP có tồn tại không
        if (sessionOTP == null || email == null) {
            model.addAttribute("errorMessage", "Phiên làm việc đã hết hạn. Vui lòng thử lại.");
            model.addAttribute("email", email);
            return "user/verify-otp";
        }

        // Kiểm tra OTP có hết hạn không (5 phút = 300000ms)
        if (System.currentTimeMillis() - otpCreatedTime > 300000) {
            model.addAttribute("errorMessage", "OTP đã hết hạn. Vui lòng yêu cầu OTP mới.");
            model.addAttribute("email", email);
            return "user/verify-otp";
        }

        // Kiểm tra OTP có đúng không
        if (!otp.equals(sessionOTP)) {
            model.addAttribute("errorMessage", "OTP không chính xác. Vui lòng thử lại.");
            model.addAttribute("email", email);
            return "user/verify-otp";
        }

        // OTP đúng - chuyển đến trang đặt lại mật khẩu
        session.setAttribute("otpVerified", true);
        log.info("✅ Xác minh OTP thành công cho email: {}", email);
        return "redirect:/reset-password";
    }

    /**
     * Hiển thị trang đặt lại mật khẩu
     * GET /reset-password
     */
    @GetMapping("/reset-password")
    public String showResetPasswordPage(HttpSession session, Model model) {
        // Kiểm tra đã xác minh OTP chưa
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        if (otpVerified == null || !otpVerified) {
            return "redirect:/forgot-password";
        }

        model.addAttribute("resetPasswordDTO", new ResetPasswordDTO());
        return "user/reset-password";
    }

    /**
     * Xử lý đặt lại mật khẩu
     * POST /reset-password
     */
    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute ResetPasswordDTO resetPasswordDTO,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "user/reset-password";
        }

        // Kiểm tra mật khẩu khớp
        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
            return "user/reset-password";
        }

        try {
            String email = (String) session.getAttribute("resetPasswordEmail");

            if (email == null) {
                return "redirect:/forgot-password";
            }

            // Đặt lại mật khẩu
            loginService.resetPassword(email, resetPasswordDTO.getNewPassword());

            // Xóa thông tin trong session
            session.removeAttribute("resetPasswordOTP");
            session.removeAttribute("resetPasswordEmail");
            session.removeAttribute("otpCreatedTime");
            session.removeAttribute("otpVerified");

            log.info("✅ Đặt lại mật khẩu thành công cho email: {}", email);

            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Đặt lại mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới.");
            return "redirect:/login";

        } catch (Exception e) {
            log.error("❌ Lỗi khi đặt lại mật khẩu: {}", e.getMessage());
            model.addAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại!");
            return "user/reset-password";
        }
    }

    /**
     * API gửi lại OTP
     * POST /api/resend-otp
     */
    @PostMapping("/api/resend-otp")
    @ResponseBody
    public Map<String, String> resendOTP(HttpSession session) {
        try {
            String email = (String) session.getAttribute("resetPasswordEmail");

            if (email == null) {
                return Map.of("status", "error", "message", "Không tìm thấy email");
            }

            // Tạo OTP mới
            String otp = String.format("%06d", new java.util.Random().nextInt(999999));

            // Cập nhật session
            session.setAttribute("resetPasswordOTP", otp);
            session.setAttribute("otpCreatedTime", System.currentTimeMillis());

            // Gửi OTP
            loginService.sendOTPEmail(email, otp);

            log.info("✅ Đã gửi lại OTP đến email: {}", email);

            return Map.of("status", "success", "message", "OTP đã được gửi lại");

        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi lại OTP: {}", e.getMessage());
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    // ============================================
    // API KIỂM TRA
    // ============================================

    /**
     * API kiểm tra email đã tồn tại
     * GET /api/check-email
     */
    @GetMapping("/api/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return loginService.emailExists(email);
    }

    /**
     * API kiểm tra số điện thoại đã tồn tại
     * GET /api/check-phone
     */
    @GetMapping("/api/check-phone")
    @ResponseBody
    public boolean checkPhone(@RequestParam String sdt) {
        return loginService.phoneExists(sdt);
    }
}