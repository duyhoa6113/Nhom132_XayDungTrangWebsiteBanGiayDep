package com.poly.repository;

import com.poly.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository để truy vấn dữ liệu sản phẩm
 *
 */
@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer>,
        JpaSpecificationExecutor<SanPham> {

    // ========== BASIC FIND METHODS ==========

    Page<SanPham> findByTrangThai(int trangThai, Pageable pageable);

    Page<SanPham> findByDanhMuc_DanhMucIdAndTrangThai(Integer danhMucId, int trangThai, Pageable pageable);

    Page<SanPham> findByDanhMuc_DanhMucIdAndSanPhamIdNotAndTrangThai(
            Integer danhMucId,
            Integer excludeId,
            Integer trangThai,
            Pageable pageable
    );

    Page<SanPham> findBySanPhamIdNotAndTrangThai(
            Integer excludeId,
            Integer trangThai,
            Pageable pageable
    );

    Optional<SanPham> findBySanPhamIdAndTrangThai(Integer sanPhamId, int trangThai);

    // ========== QUERY METHODS - PAGE ==========

    /**
     * ✅ Tìm kiếm sản phẩm theo keyword (ONLY ONE - removed duplicate)
     */
    @Query("SELECT sp FROM SanPham sp " +
            "LEFT JOIN sp.thuongHieu th " +
            "WHERE sp.trangThai = 1 " +
            "AND (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(th.ten) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SanPham> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT sp FROM SanPham sp WHERE sp.trangThai = 1 ORDER BY sp.createdAt DESC")
    Page<SanPham> findNewestProducts(Pageable pageable);

    @Query("SELECT sp FROM SanPham sp WHERE sp.danhMuc.danhMucId = :categoryId AND sp.trangThai = 1")
    Page<SanPham> findProductsByCategoryWithPage(@Param("categoryId") Integer categoryId, Pageable pageable);

    @Query("SELECT sp FROM SanPham sp WHERE sp.thuongHieu.thuongHieuId = :brandId AND sp.trangThai = 1")
    Page<SanPham> findProductsByBrand(@Param("brandId") Integer brandId, Pageable pageable);

    // ========== QUERY METHODS - LIST ==========

    @Query("SELECT sp FROM SanPham sp WHERE sp.trangThai = 1 ORDER BY sp.createdAt DESC")
    List<SanPham> findFeaturedProducts(Pageable pageable);

    @Query("SELECT DISTINCT sp FROM SanPham sp " +
            "JOIN sp.variants v " +
            "WHERE sp.trangThai = 1 AND v.giaGoc IS NOT NULL " +
            "AND v.giaGoc > v.giaBan " +
            "ORDER BY (v.giaGoc - v.giaBan) / v.giaGoc DESC")
    List<SanPham> findTopDiscountedProducts(Pageable pageable);

    @Query(value = "SELECT TOP(?1) * FROM SanPham WHERE TrangThai = 1 ORDER BY NEWID()",
            nativeQuery = true)
    List<SanPham> findRandomProducts(int limit);

    @Query("SELECT sp FROM SanPham sp WHERE sp.danhMuc.danhMucId = :categoryId AND sp.trangThai = 1")
    List<SanPham> findProductsByCategory(@Param("categoryId") Integer categoryId);

    /**
     * ✅ Autocomplete search - Top 10 results
     */
    List<SanPham> findTop10ByTenContainingIgnoreCaseAndTrangThai(String keyword, int trangThai);

    // ========== COUNT METHODS ==========

    @Query("SELECT COUNT(sp) FROM SanPham sp WHERE sp.danhMuc.danhMucId = :danhMucId AND sp.trangThai = :trangThai")
    long countByDanhMucIdAndTrangThai(@Param("danhMucId") Integer danhMucId,
                                      @Param("trangThai") int trangThai);

    @Query("SELECT COUNT(sp) FROM SanPham sp " +
            "WHERE sp.thuongHieu.thuongHieuId = :brandId " +
            "AND sp.trangThai = 1")
    long countByBrandId(@Param("brandId") Integer brandId);

    @Query("SELECT COUNT(sp) FROM SanPham sp " +
            "WHERE sp.chatLieu.chatLieuId = :materialId " +
            "AND sp.trangThai = 1")
    long countByMaterialId(@Param("materialId") Integer materialId);

    /**
     * ✅ Đếm số kết quả tìm kiếm
     */
    @Query("SELECT COUNT(sp) FROM SanPham sp " +
            "LEFT JOIN sp.thuongHieu th " +
            "WHERE sp.trangThai = 1 " +
            "AND (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(th.ten) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    long countSearchResults(@Param("keyword") String keyword);

    // ========== PRICE SORT METHODS (Native Query) ==========

    /**
     * Lấy sản phẩm theo danh mục, sắp xếp theo giá tăng dần
     */
    @Query(value = """
        SELECT sp.* FROM SanPham sp
        INNER JOIN (
            SELECT SanPhamId, MIN(GiaBan) as MinPrice
            FROM SanPhamChiTiet
            WHERE TrangThai = 1
            GROUP BY SanPhamId
        ) v ON sp.SanPhamId = v.SanPhamId
        WHERE sp.TrangThai = 1 AND sp.DanhMucId = :categoryId
        ORDER BY v.MinPrice ASC
        """,
            countQuery = """
        SELECT COUNT(sp.SanPhamId) FROM SanPham sp
        WHERE sp.TrangThai = 1 AND sp.DanhMucId = :categoryId
        """,
            nativeQuery = true)
    Page<SanPham> findByCategoryOrderByPriceAsc(@Param("categoryId") Integer categoryId, Pageable pageable);

    /**
     * Lấy sản phẩm theo danh mục, sắp xếp theo giá giảm dần
     */
    @Query(value = """
        SELECT sp.* FROM SanPham sp
        INNER JOIN (
            SELECT SanPhamId, MIN(GiaBan) as MinPrice
            FROM SanPhamChiTiet
            WHERE TrangThai = 1
            GROUP BY SanPhamId
        ) v ON sp.SanPhamId = v.SanPhamId
        WHERE sp.TrangThai = 1 AND sp.DanhMucId = :categoryId
        ORDER BY v.MinPrice DESC
        """,
            countQuery = """
        SELECT COUNT(sp.SanPhamId) FROM SanPham sp
        WHERE sp.TrangThai = 1 AND sp.DanhMucId = :categoryId
        """,
            nativeQuery = true)
    Page<SanPham> findByCategoryOrderByPriceDesc(@Param("categoryId") Integer categoryId, Pageable pageable);

    /**
     * Lấy tất cả sản phẩm active, sắp xếp theo giá tăng dần
     */
    @Query(value = """
        SELECT sp.* FROM SanPham sp
        INNER JOIN (
            SELECT SanPhamId, MIN(GiaBan) as MinPrice
            FROM SanPhamChiTiet
            WHERE TrangThai = 1
            GROUP BY SanPhamId
        ) v ON sp.SanPhamId = v.SanPhamId
        WHERE sp.TrangThai = 1
        ORDER BY v.MinPrice ASC
        """,
            countQuery = "SELECT COUNT(*) FROM SanPham WHERE TrangThai = 1",
            nativeQuery = true)
    Page<SanPham> findAllOrderByPriceAsc(Pageable pageable);

    /**
     * Lấy tất cả sản phẩm active, sắp xếp theo giá giảm dần
     */
    @Query(value = """
        SELECT sp.* FROM SanPham sp
        INNER JOIN (
            SELECT SanPhamId, MIN(GiaBan) as MinPrice
            FROM SanPhamChiTiet
            WHERE TrangThai = 1
            GROUP BY SanPhamId
        ) v ON sp.SanPhamId = v.SanPhamId
        WHERE sp.TrangThai = 1
        ORDER BY v.MinPrice DESC
        """,
            countQuery = "SELECT COUNT(*) FROM SanPham WHERE TrangThai = 1",
            nativeQuery = true)
    Page<SanPham> findAllOrderByPriceDesc(Pageable pageable);

    /**
     * Tìm sản phẩm theo tên (search)
     */
    @Query("SELECT s FROM SanPham s WHERE LOWER(s.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND s.trangThai = 1")
    Page<SanPham> searchByTen(String keyword, Pageable pageable);

    // Trong SanPhamRepository
    @Query("SELECT sp FROM SanPham sp WHERE sp.trangThai = ?1")
    Page<SanPham> findByTrangThai(Integer trangThai, Pageable pageable);
}