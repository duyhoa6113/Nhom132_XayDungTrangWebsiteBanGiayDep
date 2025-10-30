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
public class AuthService {

    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Đăng ký tài khoản mới
     */
    @Transactional
    public KhachHang register(RegisterDTO registerDTO) {
        // Kiểm tra email đã tồn tại
        if (khachHangRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Kiểm tra số điện thoại đã tồn tại
        if (khachHangRepository.existsBySdt(registerDTO.getSdt())) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }

        // Kiểm tra mật khẩu khớp
        if (!registerDTO.getMatKhau().equals(registerDTO.getXacNhanMatKhau())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        // Tạo khách hàng mới
        KhachHang khachHang = KhachHang.builder()
                .hoTen(registerDTO.getHoTen())
                .email(registerDTO.getEmail().toLowerCase())
                .sdt(registerDTO.getSdt())
                .matKhauHash(passwordEncoder.encode(registerDTO.getMatKhau()))
                .createdAt(LocalDateTime.now())
                .trangThai((byte) 1)
                .build();

        log.info("Đăng ký tài khoản mới: {}", registerDTO.getEmail());
        return khachHangRepository.save(khachHang);
    }

    /**
     * Đăng nhập
     */
    public Optional<KhachHang> login(LoginDTO loginDTO) {
        // Tìm khách hàng theo email
        Optional<KhachHang> khachHangOpt = khachHangRepository
                .findByEmailAndTrangThai(loginDTO.getEmail().toLowerCase(), (byte) 1);

        if (khachHangOpt.isEmpty()) {
            log.warn("Đăng nhập thất bại: Email không tồn tại - {}", loginDTO.getEmail());
            return Optional.empty();
        }

        KhachHang khachHang = khachHangOpt.get();

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(loginDTO.getMatKhau(), khachHang.getMatKhauHash())) {
            log.warn("Đăng nhập thất bại: Sai mật khẩu - {}", loginDTO.getEmail());
            return Optional.empty();
        }

        log.info("Đăng nhập thành công: {}", loginDTO.getEmail());
        return Optional.of(khachHang);
    }

    /**
     * Kiểm tra email đã tồn tại
     */
    public boolean emailExists(String email) {
        return khachHangRepository.existsByEmail(email.toLowerCase());
    }

    /**
     * Kiểm tra số điện thoại đã tồn tại
     */
    public boolean phoneExists(String sdt) {
        return khachHangRepository.existsBySdt(sdt);
    }

    /**
     * Lấy thông tin khách hàng theo ID
     */
    public Optional<KhachHang> getKhachHangById(Integer id) {
        return khachHangRepository.findById(id);
    }
}