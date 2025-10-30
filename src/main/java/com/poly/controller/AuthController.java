package com.poly.controller;

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
import com.poly.service.AuthService;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Hiển thị trang đăng ký
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    /**
     * Xử lý đăng ký
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDTO registerDTO,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            // Đăng ký tài khoản
            KhachHang khachHang = authService.register(registerDTO);

            // Thành công
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/auth/login";

        } catch (RuntimeException e) {
            // Lỗi
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

    /**
     * Hiển thị trang đăng nhập
     */
    @GetMapping("/login")
    public String showLoginPage(Model model, HttpSession session) {
        // Nếu đã đăng nhập, redirect về trang Index
        if (session.getAttribute("khachHang") != null) {
            return "redirect:/Index";
        }

        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }

    /**
     * Xử lý đăng nhập
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO,
                        BindingResult bindingResult,
                        Model model,
                        HttpSession session,
                        RedirectAttributes redirectAttributes,
                        @RequestParam(required = false) String redirectUrl) {

        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        // Xử lý đăng nhập
        Optional<KhachHang> khachHangOpt = authService.login(loginDTO);

        if (khachHangOpt.isEmpty()) {
            // Đăng nhập thất bại
            model.addAttribute("errorMessage", "Email hoặc mật khẩu không đúng");
            return "auth/login";
        }

        // Đăng nhập thành công
        KhachHang khachHang = khachHangOpt.get();
        session.setAttribute("khachHang", khachHang);
        session.setAttribute("khachHangId", khachHang.getKhachHangId());
        session.setAttribute("khachHangTen", khachHang.getHoTen());

        log.info("Khách hàng {} đã đăng nhập", khachHang.getEmail());

        // Thêm thông báo chào mừng
        redirectAttributes.addFlashAttribute("successMessage",
                "Đăng nhập thành công! Chào mừng " + khachHang.getHoTen());

        // Redirect về trang được yêu cầu hoặc trang home
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            return "redirect:" + redirectUrl;
        }
        return "redirect:/Index";
    }

    /**
     * Đăng xuất
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("khachHang");
        session.removeAttribute("khachHangId");
        session.removeAttribute("khachHangTen");
        session.invalidate();

        redirectAttributes.addFlashAttribute("successMessage", "Đã đăng xuất thành công");
        return "redirect:/Index";
    }

    // ============================================
    // QUÊN MẬT KHẨU - FORGOT PASSWORD
    // ============================================

    /**
     * Hiển thị trang quên mật khẩu
     * GET /auth/forgot-password
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        log.info("Displaying forgot password page");
        model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO());
        return "auth/forgot-password";
    }
    // ============================================
    // API KIỂM TRA
    // ============================================

    /**
     * API kiểm tra email đã tồn tại
     */
    @GetMapping("/api/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return authService.emailExists(email);
    }

    /**
     * API kiểm tra số điện thoại đã tồn tại
     */
    @GetMapping("/api/check-phone")
    @ResponseBody
    public boolean checkPhone(@RequestParam String sdt) {
        return authService.phoneExists(sdt);
    }
}