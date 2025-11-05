package com.poly.repository;

import com.poly.entity.DanhGia;
import com.poly.entity.KhachHang;
import com.poly.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {

    /**
     * Tìm đánh giá theo sản phẩm
     */
    Page<DanhGia> findBySanPhamSanPhamIdAndTrangThai(Integer sanPhamId, Byte trangThai, Pageable pageable);

    /**
     * Tìm đánh giá theo khách hàng
     */
    Page<DanhGia> findByKhachHangKhachHangIdOrderByCreatedAtDesc(Integer khachHangId, Pageable pageable);

    /**
     * Tìm đánh giá theo sản phẩm với thông tin chi tiết
     */
    @Query("SELECT dg FROM DanhGia dg " +
            "LEFT JOIN FETCH dg.khachHang " +
            "LEFT JOIN FETCH dg.sanPham " +
            "WHERE dg.sanPham.sanPhamId = :sanPhamId " +
            "AND dg.trangThai = 1 " +
            "ORDER BY dg.createdAt DESC")
    Page<DanhGia> findBySanPhamWithDetails(@Param("sanPhamId") Integer sanPhamId, Pageable pageable);

    /**
     * Kiểm tra khách hàng đã đánh giá sản phẩm chưa
     */
    boolean existsByKhachHangAndSanPham(KhachHang khachHang, SanPham sanPham);

    /**
     * Kiểm tra khách hàng đã đánh giá sản phẩm trong hóa đơn chưa
     */
    @Query("SELECT CASE WHEN COUNT(dg) > 0 THEN true ELSE false END " +
            "FROM DanhGia dg " +
            "WHERE dg.khachHang.khachHangId = :khachHangId " +
            "AND dg.hoaDon.hoaDonId = :hoaDonId " +
            "AND dg.sanPham.sanPhamId = :sanPhamId")
    boolean existsByKhachHangIdAndHoaDonIdAndSanPhamId(
            @Param("khachHangId") Integer khachHangId,
            @Param("hoaDonId") Integer hoaDonId,
            @Param("sanPhamId") Integer sanPhamId);

    /**
     * Tính điểm trung bình của sản phẩm
     */
    @Query("SELECT AVG(dg.diemSao) FROM DanhGia dg " +
            "WHERE dg.sanPham.sanPhamId = :sanPhamId " +
            "AND dg.trangThai = 1")
    Double calculateAverageRating(@Param("sanPhamId") Integer sanPhamId);

    /**
     * Đếm số lượng đánh giá theo sản phẩm
     */
    @Query("SELECT COUNT(dg) FROM DanhGia dg " +
            "WHERE dg.sanPham.sanPhamId = :sanPhamId " +
            "AND dg.trangThai = 1")
    long countBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    /**
     * Đếm số lượng đánh giá theo số sao
     */
    @Query("SELECT COUNT(dg) FROM DanhGia dg " +
            "WHERE dg.sanPham.sanPhamId = :sanPhamId " +
            "AND dg.diemSao = :diemSao " +
            "AND dg.trangThai = 1")
    long countBySanPhamIdAndDiemSao(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("diemSao") Byte diemSao);

    /**
     * Lấy đánh giá có hình ảnh
     */
    @Query("SELECT dg FROM DanhGia dg " +
            "WHERE dg.sanPham.sanPhamId = :sanPhamId " +
            "AND dg.hinhAnh IS NOT NULL " +
            "AND dg.trangThai = 1 " +
            "ORDER BY dg.createdAt DESC")
    Page<DanhGia> findBySanPhamIdWithImages(@Param("sanPhamId") Integer sanPhamId, Pageable pageable);

    /**
     * Lấy đánh giá theo số sao
     */
    @Query("SELECT dg FROM DanhGia dg " +
            "WHERE dg.sanPham.sanPhamId = :sanPhamId " +
            "AND dg.diemSao = :diemSao " +
            "AND dg.trangThai = 1 " +
            "ORDER BY dg.createdAt DESC")
    Page<DanhGia> findBySanPhamIdAndDiemSao(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("diemSao") Byte diemSao,
            Pageable pageable);

    /**
     * Lấy đánh giá mới nhất
     */
    @Query("SELECT dg FROM DanhGia dg " +
            "LEFT JOIN FETCH dg.khachHang " +
            "LEFT JOIN FETCH dg.sanPham " +
            "WHERE dg.trangThai = 1 " +
            "ORDER BY dg.createdAt DESC")
    List<DanhGia> findLatestReviews(Pageable pageable);

    /**
     * Lấy đánh giá theo khách hàng và sản phẩm
     */
    Optional<DanhGia> findByKhachHangAndSanPham(KhachHang khachHang, SanPham sanPham);
}
