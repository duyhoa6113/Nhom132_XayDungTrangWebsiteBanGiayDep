package com.poly.repository;

import com.poly.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho entity GioHangChiTiet
 */
@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {

    /**
     * Tìm chi tiết giỏ hàng theo giỏ hàng và variant
     */
    @Query("SELECT ct FROM GioHangChiTiet ct " +
            "WHERE ct.gioHang.gioHangId = :gioHangId " +
            "AND ct.variant.variantId = :variantId")
    Optional<GioHangChiTiet> findByGioHangIdAndVariantId(
            @Param("gioHangId") Integer gioHangId,
            @Param("variantId") Integer variantId
    );

    /**
     * Xóa tất cả chi tiết của một giỏ hàng
     */
    @Query("DELETE FROM GioHangChiTiet ct WHERE ct.gioHang.gioHangId = :gioHangId")
    void deleteAllByGioHangId(@Param("gioHangId") Integer gioHangId);
}