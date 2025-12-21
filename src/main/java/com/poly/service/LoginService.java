package com.poly.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.dto.LoginDTO;
import com.poly.dto.RegisterDTO;
import com.poly.entity.KhachHang;
import com.poly.entity.NhanVien;
import com.poly.repository.KhachHangRepository;
import com.poly.repository.NhanVienRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    // ============================================
    // ĐĂNG KÝ - REGISTER
    // ============================================

    /**
     * Đăng ký tài khoản mới
     *
     * @param registerDTO Thông tin đăng ký
     * @return KhachHang vừa tạo
     * @throws RuntimeException nếu email hoặc SDT đã tồn tại
     */
    @Transactional
    public KhachHang register(RegisterDTO registerDTO) {
        log.info("Bắt đầu đăng ký tài khoản: {}", registerDTO.getEmail());

        // Validate email
        String email = registerDTO.getEmail().toLowerCase().trim();
        if (khachHangRepository.existsByEmail(email)) {
            log.warn("Email đã tồn tại: {}", email);
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Validate số điện thoại
        String sdt = registerDTO.getSdt().trim();
        if (khachHangRepository.existsBySdt(sdt)) {
            log.warn("Số điện thoại đã tồn tại: {}", sdt);
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }

        // Kiểm tra mật khẩu khớp
        if (!registerDTO.getMatKhau().equals(registerDTO.getXacNhanMatKhau())) {
            log.warn("Mật khẩu xác nhận không khớp");
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        // Mã hóa mật khẩu
        String matKhauHash = passwordEncoder.encode(registerDTO.getMatKhau());

        // Tạo khách hàng mới
        KhachHang khachHang = KhachHang.builder()
                .hoTen(registerDTO.getHoTen().trim())
                .email(email)
                .sdt(sdt)
                .matKhauHash(matKhauHash)
                .createdAt(LocalDateTime.now())
                .trangThai(1)
                .build();

        // Lưu vào database
        khachHang = khachHangRepository.save(khachHang);

        log.info("Đăng ký tài khoản thành công: {} - ID: {}",
                email, khachHang.getKhachHangId());

        return khachHang;
    }

    // ============================================
    // ĐĂNG NHẬP - LOGIN
    // ============================================

    /**
     * Đăng nhập
     *
     * @param loginDTO Thông tin đăng nhập
     * @return Optional<KhachHang> nếu đăng nhập thành công
     */
    public Optional<KhachHang> login(LoginDTO loginDTO) {
        String email = loginDTO.getEmail().toLowerCase().trim();
        log.info("Đang xử lý đăng nhập cho: {}", email);

        // Tìm khách hàng theo email và trạng thái active
        Optional<KhachHang> khachHangOpt = khachHangRepository
                .findByEmailAndTrangThai(email, (byte) 1);

        // Kiểm tra email có tồn tại không
        if (khachHangOpt.isEmpty()) {
            log.warn("Đăng nhập thất bại: Email không tồn tại hoặc tài khoản bị khóa - {}", email);
            return Optional.empty();
        }

        KhachHang khachHang = khachHangOpt.get();

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(loginDTO.getMatKhau(), khachHang.getMatKhauHash())) {
            log.warn("Đăng nhập thất bại: Sai mật khẩu - {}", email);
            return Optional.empty();
        }

        // Đăng nhập thành công
        log.info("Đăng nhập thành công: {} - ID: {}", email, khachHang.getKhachHangId());
        return Optional.of(khachHang);
    }

    /**
     * Đăng nhập cho nhân viên (admin/employee)
     *
     * @param loginDTO Thông tin đăng nhập
     * @return Optional<NhanVien> nếu đăng nhập thành công
     */
    public Optional<NhanVien> loginNhanVien(LoginDTO loginDTO) {
        String email = loginDTO.getEmail().toLowerCase().trim();
        log.info("Đang xử lý đăng nhập nhân viên cho: {}", email);

        // Tìm nhân viên theo email
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByEmail(email);

        // Kiểm tra email có tồn tại không
        if (nhanVienOpt.isEmpty()) {
            log.warn("Đăng nhập thất bại: Email không tồn tại - {}", email);
            return Optional.empty();
        }

        NhanVien nhanVien = nhanVienOpt.get();

        // Kiểm tra trạng thái (chỉ cho phép đăng nhập nếu trạng thái = 1)
        if (nhanVien.getTrangThai() == null || nhanVien.getTrangThai() != 1) {
            log.warn("Đăng nhập thất bại: Tài khoản bị khóa - {}", email);
            return Optional.empty();
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(loginDTO.getMatKhau(), nhanVien.getMatKhauHash())) {
            log.warn("Đăng nhập thất bại: Sai mật khẩu - {}", email);
            return Optional.empty();
        }

        // Đăng nhập thành công
        log.info("Đăng nhập nhân viên thành công: {} - ID: {} - VaiTro: {}", 
                email, nhanVien.getNhanVienId(), 
                nhanVien.getVaiTro() != null ? nhanVien.getVaiTro().getVaiTroId() : "null");
        return Optional.of(nhanVien);
    }

    // ============================================
    // KIỂM TRA - VALIDATION
    // ============================================

    /**
     * Kiểm tra email đã tồn tại
     *
     * @param email Email cần kiểm tra
     * @return true nếu email đã tồn tại
     */
    public boolean emailExists(String email) {
        boolean exists = khachHangRepository.existsByEmail(email.toLowerCase().trim());
        log.debug("Kiểm tra email {}: {}", email, exists ? "đã tồn tại" : "chưa tồn tại");
        return exists;
    }

    /**
     * Kiểm tra số điện thoại đã tồn tại
     *
     * @param sdt Số điện thoại cần kiểm tra
     * @return true nếu số điện thoại đã tồn tại
     */
    public boolean phoneExists(String sdt) {
        boolean exists = khachHangRepository.existsBySdt(sdt.trim());
        log.debug("Kiểm tra SDT {}: {}", sdt, exists ? "đã tồn tại" : "chưa tồn tại");
        return exists;
    }

    // ============================================
    // QUẢN LÝ TÀI KHOẢN
    // ============================================

    /**
     * Lấy thông tin khách hàng theo ID
     *
     * @param id ID khách hàng
     * @return Optional<KhachHang>
     */
    public Optional<KhachHang> getKhachHangById(Integer id) {
        return khachHangRepository.findById(id);
    }

    /**
     * Lấy thông tin khách hàng theo email
     *
     * @param email Email khách hàng
     * @return Optional<KhachHang>
     */
    public Optional<KhachHang> getKhachHangByEmail(String email) {
        return khachHangRepository.findByEmailAndTrangThai(
                email.toLowerCase().trim(), (byte) 1);
    }

    /**
     * Cập nhật thông tin khách hàng
     *
     * @param khachHang Khách hàng cần cập nhật
     * @return KhachHang đã cập nhật
     */
    @Transactional
    public KhachHang updateKhachHang(KhachHang khachHang) {
        log.info("Cập nhật thông tin khách hàng ID: {}", khachHang.getKhachHangId());
        return khachHangRepository.save(khachHang);
    }

    /**
     * Đổi mật khẩu
     *
     * @param khachHangId ID khách hàng
     * @param matKhauCu Mật khẩu cũ
     * @param matKhauMoi Mật khẩu mới
     * @throws RuntimeException nếu mật khẩu cũ không đúng
     */
    @Transactional
    public void changePassword(Integer khachHangId, String matKhauCu, String matKhauMoi) {
        log.info("Đổi mật khẩu cho khách hàng ID: {}", khachHangId);

        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(matKhauCu, khachHang.getMatKhauHash())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        // Mã hóa và lưu mật khẩu mới
        khachHang.setMatKhauHash(passwordEncoder.encode(matKhauMoi));
        khachHangRepository.save(khachHang);

        log.info("Đổi mật khẩu thành công cho khách hàng ID: {}", khachHangId);
    }



    // ============================================
    // QUÊN MẬT KHẨU - OTP
    // ============================================


    /**
     * Gửi OTP qua email
     */
    public void sendOTPEmail(String email, String otp) throws jakarta.mail.MessagingException {
        log.info("Gửi OTP đến email: {}", email);

        jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
        org.springframework.mail.javamail.MimeMessageHelper helper =
                new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("lyducq132@gmail.com");
        helper.setTo(email);
        helper.setSubject("🔐 Mã OTP Đặt Lại Mật Khẩu - NiceSport");

        String htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; padding: 20px; border-radius: 10px; }
                    .header { background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .otp-code { font-size: 36px; font-weight: bold; color: #667eea; text-align: center; padding: 20px; background: #f0f4ff; border-radius: 10px; margin: 20px 0; letter-spacing: 5px; }
                    .content { padding: 30px; color: #333; }
                    .warning { color: #f44336; font-weight: bold; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Xác Minh OTP</h1>
                    </div>
                    <div class="content">
                        <p>Mã OTP của bạn là:</p>
                        <div class="otp-code">%s</div>
                        <p class="warning">⚠️ Mã OTP chỉ có hiệu lực trong 5 phút.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(otp);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    /**
     * Đặt lại mật khẩu
     */
    @Transactional
    public void resetPassword(String email, String newPassword) {
        log.info("Đặt lại mật khẩu cho email: {}", email);

        KhachHang khachHang = khachHangRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        khachHang.setMatKhauHash(passwordEncoder.encode(newPassword));
        khachHangRepository.save(khachHang);
    }
}