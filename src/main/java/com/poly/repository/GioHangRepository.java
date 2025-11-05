package com.poly.repository;

import com.poly.entity.GioHang;
import com.poly.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho entity GioHang
 */
@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    /**
     * Tìm giỏ hàng đang active (chưa đặt hàng) của khách hàng
     */
    @Query("SELECT gh FROM GioHang gh " +
            "WHERE gh.khachHang.khachHangId = :khachHangId " +
            "AND gh.trangThai = 0")
    Optional<GioHang> findActiveCartByKhachHangId(@Param("khachHangId") Integer khachHangId);

    /**
     * Tìm giỏ hàng đang active của khách hàng kèm theo chi tiết
     */
    @Query("SELECT DISTINCT gh FROM GioHang gh " +
            "LEFT JOIN FETCH gh.chiTietList ct " +
            "LEFT JOIN FETCH ct.variant v " +
            "LEFT JOIN FETCH v.sanPham " +
            "LEFT JOIN FETCH v.mauSac " +
            "LEFT JOIN FETCH v.kichThuoc " +
            "WHERE gh.khachHang.khachHangId = :khachHangId " +
            "AND gh.trangThai = 0")
    Optional<GioHang> findActiveCartWithDetailsByKhachHangId(@Param("khachHangId") Integer khachHangId);

    /**
     * Đếm số lượng sản phẩm trong giỏ hàng của khách hàng
     */
    @Query("SELECT COALESCE(SUM(ct.soLuong), 0) FROM GioHang gh " +
            "JOIN gh.chiTietList ct " +
            "WHERE gh.khachHang.khachHangId = :khachHangId " +
            "AND gh.trangThai = 0")
    Integer countItemsByKhachHangId(@Param("khachHangId") Integer khachHangId);
}