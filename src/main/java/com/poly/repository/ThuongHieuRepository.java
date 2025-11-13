package com.poly.repository;

import com.poly.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository để truy vấn dữ liệu thương hiệu
 * ✅ FIXED: Removed duplicates & fixed query without relationship
 *
 * @author Nhóm 132
 */
@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Integer> {

    // ========== BASIC CRUD METHODS ==========

    /**
     * Tìm thương hiệu theo trạng thái
     */
    List<ThuongHieu> findByTrangThai(int trangThai);

    /**
     * Tìm thương hiệu theo trạng thái và sắp xếp theo tên
     */
    List<ThuongHieu> findByTrangThaiOrderByTenAsc(int trangThai);

    /**
     * Tìm thương hiệu theo ID và trạng thái
     */
    Optional<ThuongHieu> findByThuongHieuIdAndTrangThai(Integer thuongHieuId, int trangThai);

    /**
     * Tìm thương hiệu theo tên
     */
    Optional<ThuongHieu> findByTen(String ten);

    // ========== CATEGORY PAGE METHODS ==========

    /**
     * Lấy thương hiệu có trong danh mục kèm số lượng sản phẩm
     * Dùng cho filter sidebar trong category page
     */
    @Query("""
        SELECT th.thuongHieuId as thuongHieuId,
               th.ten as ten,
               COUNT(sp.sanPhamId) as productCount
        FROM ThuongHieu th
        INNER JOIN SanPham sp ON th.thuongHieuId = sp.thuongHieu.thuongHieuId
        WHERE sp.danhMuc.danhMucId = :categoryId 
          AND sp.trangThai = 1 
          AND th.trangThai = 1
        GROUP BY th.thuongHieuId, th.ten
        HAVING COUNT(sp.sanPhamId) > 0
        ORDER BY th.ten ASC
    """)
    List<BrandWithCount> findBrandsByCategoryWithCount(@Param("categoryId") Integer categoryId);

    /**
     * Lấy tất cả thương hiệu kèm số lượng sản phẩm
     */
    @Query("""
        SELECT th.thuongHieuId as thuongHieuId,
               th.ten as ten,
               COUNT(sp.sanPhamId) as productCount
        FROM ThuongHieu th
        LEFT JOIN SanPham sp ON th.thuongHieuId = sp.thuongHieu.thuongHieuId AND sp.trangThai = 1
        WHERE th.trangThai = 1
        GROUP BY th.thuongHieuId, th.ten
        ORDER BY th.ten ASC
    """)
    List<BrandWithCount> findAllBrandsWithCount();

    /**
     * Lấy thương hiệu có sản phẩm (không lấy thương hiệu rỗng)
     */
    @Query("SELECT DISTINCT th FROM ThuongHieu th " +
            "INNER JOIN SanPham sp ON th.thuongHieuId = sp.thuongHieu.thuongHieuId " +
            "WHERE th.trangThai = 1 " +
            "AND sp.trangThai = 1 " +
            "ORDER BY th.ten ASC")
    List<ThuongHieu> findBrandsWithProducts();

    // ========== SEARCH PAGE METHODS ==========

    /**
     * ✅ FIXED: Tìm thương hiệu theo keyword (cho search page)
     * Không dùng th.sanPhams vì không có relationship mapping
     */
    @Query("SELECT DISTINCT th FROM ThuongHieu th " +
            "INNER JOIN SanPham sp ON th.thuongHieuId = sp.thuongHieu.thuongHieuId " +
            "WHERE sp.trangThai = 1 " +
            "AND (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(th.ten) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND th.trangThai = 1 " +
            "ORDER BY th.ten")
    List<ThuongHieu> findBrandsByKeyword(@Param("keyword") String keyword);

    /**
     * Tìm thương hiệu theo tên (search trong admin)
     * Chỉ search theo tên thương hiệu
     */
    @Query("SELECT th FROM ThuongHieu th " +
            "WHERE LOWER(th.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND th.trangThai = 1 " +
            "ORDER BY th.ten ASC")
    List<ThuongHieu> searchBrandsByName(@Param("keyword") String keyword);

    // ========== COUNT METHODS ==========

    /**
     * Đếm số sản phẩm theo thương hiệu
     */
    @Query("SELECT COUNT(sp) FROM SanPham sp " +
            "WHERE sp.thuongHieu.thuongHieuId = :brandId " +
            "AND sp.trangThai = 1")
    long countProductsByBrand(@Param("brandId") Integer brandId);

    // ========== TOP BRANDS ==========

    /**
     * Lấy top thương hiệu có nhiều sản phẩm nhất
     */
    @Query(value = """
        SELECT TOP(:limit) th.* 
        FROM ThuongHieu th
        INNER JOIN SanPham sp ON th.ThuongHieuId = sp.ThuongHieuId
        WHERE th.TrangThai = 1 AND sp.TrangThai = 1
        GROUP BY th.ThuongHieuId, th.Ten, th.MoTa, th.TrangThai, th.CreatedAt
        ORDER BY COUNT(sp.SanPhamId) DESC
    """, nativeQuery = true)
    List<ThuongHieu> findTopBrandsByProductCount(@Param("limit") int limit);
}