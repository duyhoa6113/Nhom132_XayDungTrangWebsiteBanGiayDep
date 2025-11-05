package com.poly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.dto.LoginDTO;
import com.poly.dto.RegisterDTO;
import com.poly.entity.KhachHang;
import com.poly.repository.KhachHangRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;

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
                .trangThai((byte) 1) // 1 = active, 0 = inactive
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
    // QUÊN MẬT KHẨU (TODO: Implement với Email Service)
    // ============================================

    /**
     * Gửi email khôi phục mật khẩu
     * TODO: Implement với Email Service
     *
     * @param email Email khách hàng
     */
    public void sendResetPasswordEmail(String email) {
        log.info("Gửi email khôi phục mật khẩu cho: {}", email);

        Optional<KhachHang> khachHangOpt = getKhachHangByEmail(email);
        if (khachHangOpt.isEmpty()) {
            throw new RuntimeException("Email không tồn tại trong hệ thống");
        }

        // TODO: Generate reset token
        // TODO: Save token to database
        // TODO: Send email with reset link

        log.warn("Chức năng gửi email chưa được implement");
    }
}