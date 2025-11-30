package com.poly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.entity.HoaDonChiTiet;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {

    /**
     * Tìm chi tiết đơn hàng theo hóa đơn ID
     */
    @Query("SELECT hd FROM HoaDonChiTiet hd WHERE hd.hoaDon.hoaDonId = :hoaDonId")
    List<HoaDonChiTiet> findByHoaDonId(@Param("hoaDonId") Integer hoaDonId);

    /**
     * Top 10 sản phẩm bán chạy nhất (theo số lượng đơn hàng)
     * Returns: Object[] với [sanPhamId, ten, soLuongBan]
     */
    @Query(value = """
        SELECT TOP 10
            sp.SanPhamId as sanPhamId,
            sp.Ten as ten,
            SUM(hdct.SoLuong) as soLuongBan
        FROM HoaDonChiTiet hdct
        INNER JOIN SanPhamChiTiet spct ON hdct.VariantId = spct.VariantId
        INNER JOIN SanPham sp ON spct.SanPhamId = sp.SanPhamId
        INNER JOIN HoaDon hd ON hdct.HoaDonId = hd.HoaDonId
        WHERE hd.TrangThai NOT IN ('Huy', 'DaHuy')
        GROUP BY sp.SanPhamId, sp.Ten
        ORDER BY SUM(hdct.SoLuong) DESC
        """, nativeQuery = true)
    List<Object[]> findTop10BestSellingProducts();
}