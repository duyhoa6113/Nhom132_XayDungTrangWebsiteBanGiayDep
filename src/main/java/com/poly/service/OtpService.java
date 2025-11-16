package com.poly.service;

import com.poly.dto.OtpData;
import com.poly.entity.KhachHang;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final EmailService emailService;
    private final KhachHangService khachHangService;

    private static final SecureRandom random = new SecureRandom();
    private static final String OTP_SESSION_KEY = "EMAIL_CHANGE_OTP";

    /**
     * Tạo mã OTP 6 số
     */
    private String generateOtpCode() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Gửi OTP để thay đổi email - NHẬN 3 THAM SỐ
     */
    public void sendOtpForEmailChange(Integer khachHangId, String newEmail, HttpSession session) {
        KhachHang khachHang = khachHangService.findById(khachHangId);

        // Tạo OTP
        String otpCode = generateOtpCode();

        // Lưu vào session
        OtpData otpData = new OtpData();
        otpData.setEmail(newEmail);
        otpData.setOtpCode(otpCode);
        otpData.setKhachHangId(khachHangId);
        otpData.setCreatedAt(LocalDateTime.now());
        otpData.setExpiredAt(LocalDateTime.now().plusMinutes(5)); // 5 phút

        session.setAttribute(OTP_SESSION_KEY, otpData);

        // Gửi email
        emailService.sendOtpEmail(newEmail, otpCode, khachHang.getHoTen());

        log.info("Đã gửi OTP tới email: {} cho khách hàng: {}", newEmail, khachHangId);
    }

    /**
     * Xác thực OTP
     */
    public boolean verifyOtp(String email, String otpCode, HttpSession session) {
        OtpData otpData = (OtpData) session.getAttribute(OTP_SESSION_KEY);

        if (otpData == null) {
            log.warn("Không tìm thấy OTP trong session");
            return false;
        }

        if (!otpData.getEmail().equals(email)) {
            log.warn("Email không khớp");
            return false;
        }

        if (otpData.isExpired()) {
            log.warn("OTP đã hết hạn");
            session.removeAttribute(OTP_SESSION_KEY);
            return false;
        }

        if (!otpData.isValid(otpCode)) {
            log.warn("Mã OTP không đúng");
            return false;
        }

        // Xóa OTP sau khi verify thành công
        session.removeAttribute(OTP_SESSION_KEY);
        return true;
    }
}