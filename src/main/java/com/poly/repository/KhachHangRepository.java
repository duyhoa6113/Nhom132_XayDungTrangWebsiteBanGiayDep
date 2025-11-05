package com.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.entity.KhachHang;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    /**
     * Tìm khách hàng theo email
     */
    Optional<KhachHang> findByEmail(String email);

    /**
     * Tìm khách hàng theo email và trạng thái
     * (dùng trong login)
     */
    Optional<KhachHang> findByEmailAndTrangThai(String email, Byte trangThai);

    /**
     * Tìm khách hàng theo số điện thoại
     */
    Optional<KhachHang> findBySdt(String sdt);

    /**
     * Kiểm tra email đã tồn tại
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra số điện thoại đã tồn tại
     */
    boolean existsBySdt(String sdt);

    // ============================================
    // PHƯƠNG THỨC CHO RESET PASSWORD
    // ============================================

    /**
     * Tìm khách hàng theo reset token
     */
    Optional<KhachHang> findByResetToken(String resetToken);

    /**
     * Kiểm tra reset token có tồn tại không
     */
    boolean existsByResetToken(String resetToken);
}