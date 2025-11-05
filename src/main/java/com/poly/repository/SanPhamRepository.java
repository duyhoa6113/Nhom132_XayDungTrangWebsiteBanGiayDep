package com.poly.repository;

import com.poly.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    /**
     * Tìm sản phẩm theo trạng thái
     */
    Page<SanPham> findByTrangThai(Byte trangThai, Pageable pageable);

    /**
     * Tìm sản phẩm theo danh mục
     */
    Page<SanPham> findByDanhMucDanhMucId(Integer danhMucId, Pageable pageable);

    /**
     * Tìm sản phẩm theo thương hiệu
     */
    Page<SanPham> findByThuongHieuThuongHieuId(Integer thuongHieuId, Pageable pageable);

    /**
     * Lấy tất cả sản phẩm với thông tin chi tiết
     */
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
            "LEFT JOIN FETCH sp.danhMuc " +
            "LEFT JOIN FETCH sp.thuongHieu " +
            "LEFT JOIN FETCH sp.chatLieu " +
            "WHERE sp.trangThai = 1 " +
            "ORDER BY sp.createdAt DESC")
    Page<SanPham> findAllWithDetails(Pageable pageable);


    /**
     * Lấy sản phẩm theo ID với thông tin chi tiết
     */
    @Query("SELECT sp FROM SanPham sp " +
            "LEFT JOIN FETCH sp.danhMuc " +
            "LEFT JOIN FETCH sp.thuongHieu " +
            "LEFT JOIN FETCH sp.chatLieu " +
            "WHERE sp.sanPhamId = :id")
    Optional<SanPham> findByIdWithDetails(@Param("id") Integer id);

    /**
     * Tìm kiếm sản phẩm
     */
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
            "LEFT JOIN sp.thuongHieu th " +
            "LEFT JOIN sp.danhMuc dm " +
            "WHERE sp.trangThai = 1 AND (" +
            "LOWER(sp.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(th.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(dm.ten) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SanPham> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Lấy sản phẩm theo khoảng giá
     */
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
            "JOIN sp.sanPhamChiTiets spct " +
            "WHERE sp.trangThai = 1 " +
            "AND spct.giaBan BETWEEN :minPrice AND :maxPrice")
    Page<SanPham> findByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Lấy sản phẩm mới nhất
     */
    @Query("SELECT sp FROM SanPham sp " +
            "WHERE sp.trangThai = 1 " +
            "ORDER BY sp.createdAt DESC")
    List<SanPham> findLatestProducts(@Param("limit") int limit);

    /**
     * Lấy sản phẩm bán chạy
     */
    @Query("SELECT sp FROM SanPham sp " +
            "LEFT JOIN HoaDonChiTiet hdct ON hdct.variant.sanPham.sanPhamId = sp.sanPhamId " +
            "WHERE sp.trangThai = 1 " +
            "GROUP BY sp.sanPhamId " +
            "ORDER BY SUM(hdct.soLuong) DESC")
    List<SanPham> findBestSellingProducts(@Param("limit") int limit);

    /**
     * Lấy sản phẩm liên quan
     */
    @Query("SELECT sp FROM SanPham sp " +
            "WHERE sp.danhMuc.danhMucId = :danhMucId " +
            "AND sp.sanPhamId != :excludeId " +
            "AND sp.trangThai = 1 " +
            "ORDER BY sp.createdAt DESC")
    List<SanPham> findRelatedProducts(
            @Param("danhMucId") Integer danhMucId,
            @Param("excludeId") Integer excludeId,
            @Param("limit") int limit);

    /**
     * Lấy sản phẩm đang giảm giá
     */
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
            "JOIN sp.sanPhamChiTiets spct " +
            "WHERE sp.trangThai = 1 " +
            "AND spct.giaGoc IS NOT NULL " +
            "AND spct.giaBan < spct.giaGoc")
    Page<SanPham> findDiscountedProducts(Pageable pageable);

    /**
     * Lọc sản phẩm nâng cao
     */
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
            "LEFT JOIN sp.sanPhamChiTiets spct " +
            "WHERE sp.trangThai = 1 " +
            "AND (:danhMucId IS NULL OR sp.danhMuc.danhMucId = :danhMucId) " +
            "AND (:thuongHieuId IS NULL OR sp.thuongHieu.thuongHieuId = :thuongHieuId) " +
            "AND (:minPrice IS NULL OR spct.giaBan >= :minPrice) " +
            "AND (:maxPrice IS NULL OR spct.giaBan <= :maxPrice)")
    Page<SanPham> filterProducts(
            @Param("danhMucId") Integer danhMucId,
            @Param("thuongHieuId") Integer thuongHieuId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}
