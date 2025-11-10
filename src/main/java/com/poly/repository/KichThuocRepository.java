package com.poly.repository;

import com.poly.entity.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// ==================== KichThuocRepository ====================
@Repository
public interface KichThuocRepository extends JpaRepository<KichThuoc, Integer> {

    /**
     * Tìm kích thước theo trạng thái - FIXED
     */
    List<KichThuoc> findByTrangThaiOrderByTenAsc(int trangThai);

    /**
     * Lấy kích thước có trong danh mục
     */
    @Query("""
        SELECT DISTINCT kt
        FROM KichThuoc kt
        INNER JOIN SanPhamChiTiet spct ON kt.kichThuocId = spct.kichThuoc.kichThuocId
        INNER JOIN SanPham sp ON spct.sanPham.sanPhamId = sp.sanPhamId
        WHERE sp.danhMuc.danhMucId = :categoryId
          AND sp.trangThai = 1
          AND spct.trangThai = 1
          AND kt.trangThai = 1
        ORDER BY kt.ten ASC
    """)
    List<KichThuoc> findSizesByCategory(Integer categoryId);
}