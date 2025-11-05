package com.poly.repository;

import com.poly.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {

    /**
     * Tìm tất cả variants của một sản phẩm (có JOIN để lấy luôn thông tin màu sắc và kích thước)
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "LEFT JOIN FETCH spct.mauSac " +
            "LEFT JOIN FETCH spct.kichThuoc " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.trangThai = 1 " +
            "ORDER BY spct.mauSac.ten, spct.kichThuoc.ten")
    List<SanPhamChiTiet> findBySanPhamIdWithDetails(@Param("sanPhamId") Integer sanPhamId);

    /**
     * Tìm variant cụ thể theo sản phẩm, màu sắc và kích thước
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.mauSac.mauSacId = :mauSacId " +
            "AND spct.kichThuoc.kichThuocId = :kichThuocId " +
            "AND spct.trangThai = 1")
    SanPhamChiTiet findByProductColorSize(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("mauSacId") Integer mauSacId,
            @Param("kichThuocId") Integer kichThuocId
    );
}