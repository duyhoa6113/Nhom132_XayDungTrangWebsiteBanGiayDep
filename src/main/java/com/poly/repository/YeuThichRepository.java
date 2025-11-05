package com.poly.repository;

import com.poly.entity.YeuThich;
import com.poly.entity.KhachHang;
import com.poly.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YeuThichRepository extends JpaRepository<YeuThich, Integer> {

    /**
     * Tìm tất cả sản phẩm yêu thích của khách hàng, sắp xếp theo ngày thêm mới nhất
     */
    @Query("SELECT y FROM YeuThich y " +
            "JOIN FETCH y.sanPham sp " +
            "LEFT JOIN FETCH sp.thuongHieu " +
            "LEFT JOIN FETCH sp.danhMuc " +
            "WHERE y.khachHang.khachHangId = :khachHangId " +
            "ORDER BY y.createdAt DESC")
    List<YeuThich> findByKhachHangIdOrderByCreatedAtDesc(@Param("khachHangId") Integer khachHangId);

    /**
     * Tìm tất cả sản phẩm yêu thích của khách hàng (overload)
     */
    List<YeuThich> findByKhachHangOrderByCreatedAtDesc(KhachHang khachHang);

    /**
     * Kiểm tra sản phẩm đã được yêu thích chưa
     */
    boolean existsByKhachHangAndSanPham(KhachHang khachHang, SanPham sanPham);

    /**
     * Kiểm tra theo ID
     */
    @Query("SELECT CASE WHEN COUNT(y) > 0 THEN true ELSE false END " +
            "FROM YeuThich y " +
            "WHERE y.khachHang.khachHangId = :khachHangId " +
            "AND y.sanPham.sanPhamId = :sanPhamId")
    boolean existsByKhachHangIdAndSanPhamId(
            @Param("khachHangId") Integer khachHangId,
            @Param("sanPhamId") Integer sanPhamId);

    /**
     * Tìm một mục yêu thích cụ thể
     */
    Optional<YeuThich> findByKhachHangAndSanPham(KhachHang khachHang, SanPham sanPham);

    /**
     * Tìm theo ID
     */
    @Query("SELECT y FROM YeuThich y " +
            "WHERE y.khachHang.khachHangId = :khachHangId " +
            "AND y.sanPham.sanPhamId = :sanPhamId")
    Optional<YeuThich> findByKhachHangIdAndSanPhamId(
            @Param("khachHangId") Integer khachHangId,
            @Param("sanPhamId") Integer sanPhamId);

    /**
     * Đếm số lượng sản phẩm yêu thích của khách hàng
     */
    long countByKhachHang(KhachHang khachHang);

    /**
     * Đếm theo ID
     */
    @Query("SELECT COUNT(y) FROM YeuThich y WHERE y.khachHang.khachHangId = :khachHangId")
    long countByKhachHangId(@Param("khachHangId") Integer khachHangId);

    /**
     * Xóa theo khách hàng và sản phẩm
     */
    void deleteByKhachHangAndSanPham(KhachHang khachHang, SanPham sanPham);

    /**
     * Xóa theo ID
     */
    @Query("DELETE FROM YeuThich y " +
            "WHERE y.khachHang.khachHangId = :khachHangId " +
            "AND y.sanPham.sanPhamId = :sanPhamId")
    void deleteByKhachHangIdAndSanPhamId(
            @Param("khachHangId") Integer khachHangId,
            @Param("sanPhamId") Integer sanPhamId);

    /**
     * Lấy danh sách ID sản phẩm yêu thích của khách hàng
     */
    @Query("SELECT y.sanPham.sanPhamId FROM YeuThich y WHERE y.khachHang.khachHangId = :khachHangId")
    List<Integer> findSanPhamIdsByKhachHangId(@Param("khachHangId") Integer khachHangId);

    /**
     * Lấy danh sách sản phẩm yêu thích với thông tin đầy đủ
     */
    @Query("SELECT y.sanPham FROM YeuThich y " +
            "WHERE y.khachHang.khachHangId = :khachHangId " +
            "ORDER BY y.createdAt DESC")
    List<SanPham> findSanPhamsByKhachHangId(@Param("khachHangId") Integer khachHangId);

    /**
     * Xóa tất cả yêu thích của khách hàng
     */
    void deleteByKhachHang(KhachHang khachHang);

    /**
     * Xóa tất cả yêu thích của sản phẩm
     */
    void deleteBySanPham(SanPham sanPham);
}
