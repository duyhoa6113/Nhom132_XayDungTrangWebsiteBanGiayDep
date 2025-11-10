package com.poly.repository;

import com.poly.entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Integer> {

    /**
     * Tìm màu sắc theo trạng thái - FIXED
     */
    List<MauSac> findByTrangThaiOrderByTenAsc(int trangThai);

    /**
     * Lấy màu sắc có trong danh mục
     */
    @Query("""
        SELECT DISTINCT ms
        FROM MauSac ms
        INNER JOIN SanPhamChiTiet spct ON ms.mauSacId = spct.mauSac.mauSacId
        INNER JOIN SanPham sp ON spct.sanPham.sanPhamId = sp.sanPhamId
        WHERE sp.danhMuc.danhMucId = :categoryId
          AND sp.trangThai = 1
          AND spct.trangThai = 1
          AND ms.trangThai = 1
        ORDER BY ms.ten ASC
    """)
    List<MauSac> findColorsByCategory(Integer categoryId);
}