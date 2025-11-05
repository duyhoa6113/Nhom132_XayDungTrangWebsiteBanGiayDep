package com.poly.controller.user;

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
     * Xử lý yêu cầu quên mật khẩu (gửi email)
     * POST /forgot-password
     */
    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @ModelAttribute ForgotPasswordDTO forgotPasswordDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "user/forgot-password";
        }

        try {
            // TODO: Implement forgot password logic
            // loginService.sendResetPasswordEmail(forgotPasswordDTO.getEmail());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã gửi email khôi phục mật khẩu. Vui lòng kiểm tra hộp thư!");
            return "redirect:/login";

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/forgot-password";
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