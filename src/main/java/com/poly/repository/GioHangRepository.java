package com.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.entity.GioHang;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    /**
     * Tìm giỏ hàng đang mở (chưa đặt hàng) của khách hàng
     */
    Optional<GioHang> findByKhachHang_KhachHangIdAndTrangThai(Integer khachHangId, Byte trangThai);

    /**
     * Tìm giỏ hàng đang hoạt động của khách hàng
     */
    default Optional<GioHang> findActiveCartByCustomerId(Integer khachHangId) {
        return findByKhachHang_KhachHangIdAndTrangThai(khachHangId, (byte) 0);
    }
}