package com.poly.service;

import com.poly.dto.ChangePasswordDTO;
import com.poly.dto.ProfileUpdateDTO;
import com.poly.entity.KhachHang;
import com.poly.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/avatars/";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

    /**
     * Lấy thông tin khách hàng theo ID
     */
    public KhachHang getKhachHangById(Integer id) {
        return khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    /**
     * Cập nhật thông tin hồ sơ
     */
    @Transactional
    public void updateProfile(Integer khachHangId, ProfileUpdateDTO dto) {
        KhachHang khachHang = getKhachHangById(khachHangId);

        // Kiểm tra email đã tồn tại (nếu thay đổi)
        if (dto.getEmail() != null && !dto.getEmail().equals(khachHang.getEmail())) {
            if (khachHangRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email đã tồn tại");
            }
        }

        // Kiểm tra số điện thoại đã tồn tại (nếu thay đổi)
        if (dto.getSdt() != null && !dto.getSdt().equals(khachHang.getSdt())) {
            if (khachHangRepository.existsBySdt(dto.getSdt())) {
                throw new RuntimeException("Số điện thoại đã tồn tại");
            }
        }

        // Cập nhật thông tin
        khachHang.setHoTen(dto.getHoTen());
        khachHang.setEmail(dto.getEmail());
        khachHang.setSdt(dto.getSdt());
        khachHang.setNgaySinh(dto.getNgaySinh());
        khachHang.setGioiTinh(dto.getGioiTinh());

        khachHangRepository.save(khachHang);
    }

    /**
     * Upload avatar
     */
    @Transactional
    public String uploadAvatar(Integer khachHangId, MultipartFile file) {
        KhachHang khachHang = getKhachHangById(khachHangId);

        // Kiểm tra file
        if (file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }

        // Kiểm tra dung lượng file
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Dung lượng file vượt quá 2MB");
        }

        // Kiểm tra định dạng file
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Chỉ chấp nhận file ảnh (JPG, PNG)");
        }

        try {
            // Tạo thư mục nếu chưa tồn tại
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Tạo tên file unique
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            // Lưu file
            Path path = Paths.get(UPLOAD_DIR + newFilename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Xóa avatar cũ nếu có
            if (khachHang.getAvatar() != null && !khachHang.getAvatar().isEmpty()) {
                deleteOldAvatar(khachHang.getAvatar());
            }

            // Cập nhật đường dẫn avatar
            String avatarUrl = "/uploads/avatars/" + newFilename;
            khachHang.setAvatar(avatarUrl);
            khachHangRepository.save(khachHang);

            return avatarUrl;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload file: " + e.getMessage());
        }
    }

    /**
     * Đổi mật khẩu
     */
    @Transactional
    public void changePassword(Integer khachHangId, ChangePasswordDTO dto) {
        KhachHang khachHang = getKhachHangById(khachHangId);

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(dto.getCurrentPassword(), khachHang.getMatKhauHash())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        // Kiểm tra mật khẩu mới khớp
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu mới không khớp");
        }

        // Kiểm tra độ dài mật khẩu
        if (dto.getNewPassword().length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        }

        // Cập nhật mật khẩu
        khachHang.setMatKhauHash(passwordEncoder.encode(dto.getNewPassword()));
        khachHangRepository.save(khachHang);
    }

    /**
     * Xóa avatar cũ
     */
    private void deleteOldAvatar(String avatarUrl) {
        try {
            String filename = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
            Path path = Paths.get(UPLOAD_DIR + filename);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Không thể xóa avatar cũ: " + e.getMessage());
        }
    }
}