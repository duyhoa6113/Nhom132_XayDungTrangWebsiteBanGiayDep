package com.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.entity.GioHangChiTiet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {

    /**
     * Tìm tất cả items trong một giỏ hàng
     */
    List<GioHangChiTiet> findByGioHang_GioHangId(Integer gioHangId);

    /**
     * Tìm một item cụ thể trong giỏ hàng
     */
    Optional<GioHangChiTiet> findByGioHang_GioHangIdAndVariant_VariantId(
            Integer gioHangId,
            Integer variantId
    );

    /**
     * Đếm số items trong giỏ hàng
     */
    @Query("SELECT COUNT(ghct) FROM GioHangChiTiet ghct WHERE ghct.gioHang.gioHangId = :gioHangId")
    Long countByGioHangId(@Param("gioHangId") Integer gioHangId);

    /**
     * Tính tổng tiền trong giỏ hàng
     */
    @Query("SELECT COALESCE(SUM(ghct.thanhTien), 0) FROM GioHangChiTiet ghct WHERE ghct.gioHang.gioHangId = :gioHangId")
    BigDecimal getTotalAmount(@Param("gioHangId") Integer gioHangId);

    /**
     * Xóa tất cả items trong giỏ hàng
     */
    void deleteByGioHang_GioHangId(Integer gioHangId);
}