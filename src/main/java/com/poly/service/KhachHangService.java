package com.poly.service;

import com.poly.dto.ProfileUpdateRequest;
import com.poly.entity.KhachHang;
import com.poly.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;

    // Thư mục upload (trong thư mục static)
    @Value("${upload.path:src/main/resources/static/uploads/avatars}")
    private String uploadPath;

    /**
     * Tìm khách hàng theo ID
     */
    public KhachHang findById(Integer id) {
        return khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    /**
     * Cập nhật thông tin cá nhân
     */
    @Transactional
    public KhachHang updateProfile(Integer khachHangId, ProfileUpdateRequest request) {
        KhachHang khachHang = findById(khachHangId);

        // Cập nhật thông tin (chỉ cập nhật những field không null)
        if (request.getHoTen() != null && !request.getHoTen().trim().isEmpty()) {
            khachHang.setHoTen(request.getHoTen().trim());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Kiểm tra email đã tồn tại chưa (trừ email của chính mình)
            if (!khachHang.getEmail().equals(request.getEmail())) {
                if (khachHangRepository.existsByEmail(request.getEmail())) {
                    throw new RuntimeException("Email đã được sử dụng");
                }
                khachHang.setEmail(request.getEmail().trim());
            }
        }

        if (request.getSdt() != null && !request.getSdt().trim().isEmpty()) {
            khachHang.setSdt(request.getSdt().trim());
        }

        if (request.getNgaySinh() != null) {
            khachHang.setNgaySinh(request.getNgaySinh());
        }

        if (request.getGioiTinh() != null && !request.getGioiTinh().trim().isEmpty()) {
            khachHang.setGioiTinh(request.getGioiTinh());
        }

        khachHang.setUpdatedAt(LocalDateTime.now());

        return khachHangRepository.save(khachHang);
    }

    /**
     * Đổi mật khẩu
     */
    @Transactional
    public void changePassword(Integer khachHangId, String currentPassword, String newPassword, String confirmPassword) {
        KhachHang khachHang = findById(khachHangId);

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, khachHang.getMatKhauHash())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        // Kiểm tra mật khẩu mới
        if (newPassword.length() < 6) {
            throw new RuntimeException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        // Kiểm tra xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Xác nhận mật khẩu không khớp");
        }

        // Cập nhật mật khẩu
        khachHang.setMatKhauHash(passwordEncoder.encode(newPassword));
        khachHang.setUpdatedAt(LocalDateTime.now());

        khachHangRepository.save(khachHang);

        log.info("Đổi mật khẩu thành công cho khách hàng: {}", khachHangId);
    }

    /**
     * Upload avatar - SỬA LẠI
     */
    @Transactional
    public String uploadAvatar(Integer khachHangId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }

        // Kiểm tra định dạng file
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Chỉ chấp nhận file ảnh (JPEG, PNG)");
        }

        // Kiểm tra kích thước file (max 1MB)
        if (file.getSize() > 1 * 1024 * 1024) {
            throw new RuntimeException("Kích thước file không được vượt quá 1MB");
        }

        try {
            // Tạo tên file unique
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;

            // Tạo thư mục nếu chưa tồn tại
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Lưu file
            Path filePath = Paths.get(uploadPath, fileName);
            Files.write(filePath, file.getBytes());

            // Cập nhật database
            KhachHang khachHang = findById(khachHangId);
            String avatarUrl = "/uploads/avatars/" + fileName;
            khachHang.setAvatar(avatarUrl);
            khachHang.setUpdatedAt(LocalDateTime.now());
            khachHangRepository.save(khachHang);

            log.info("Upload avatar thành công: {}", fileName);

            return avatarUrl;

        } catch (IOException e) {
            log.error("Lỗi khi upload avatar", e);
            throw new RuntimeException("Không thể upload ảnh: " + e.getMessage());
        }
    }

    /**
     * Thay đổi số điện thoại
     */
    @Transactional
    public void changePhone(Integer khachHangId, String newPhone, String password) {
        KhachHang khachHang = findById(khachHangId);

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(password, khachHang.getMatKhauHash())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        // Validate phone format
        if (!newPhone.matches("^[0-9]{10,11}$")) {
            throw new RuntimeException("Số điện thoại phải có 10-11 chữ số");
        }

        // Cập nhật số điện thoại
        khachHang.setSdt(newPhone);
        khachHang.setUpdatedAt(LocalDateTime.now());
        khachHangRepository.save(khachHang);

        log.info("Thay đổi số điện thoại thành công cho khách hàng: {}", khachHangId);
    }
}