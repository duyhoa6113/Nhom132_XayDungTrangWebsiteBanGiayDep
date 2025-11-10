package com.poly.repository;

import com.poly.dto.BrandWithCount;
import com.poly.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository để truy vấn dữ liệu thương hiệu
 *
 * @author Nhóm 132
 */
@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Integer> {

    /**
     * Tìm thương hiệu theo trạng thái
     * @param trangThai - 1: active, 0: inactive
     * @return List thương hiệu
     */
    List<ThuongHieu> findByTrangThai(int trangThai);

    /**
     * Tìm thương hiệu theo trạng thái và sắp xếp theo tên
     * @param trangThai - 1: active, 0: inactive
     * @return List thương hiệu đã sắp xếp
     */
    List<ThuongHieu> findByTrangThaiOrderByTenAsc(int trangThai);

    /**
     * Tìm thương hiệu theo ID và trạng thái
     * @param thuongHieuId - ID thương hiệu
     * @param trangThai - 1: active, 0: inactive
     * @return Optional thương hiệu
     */
    Optional<ThuongHieu> findByThuongHieuIdAndTrangThai(Integer thuongHieuId, int trangThai);

    /**
     * Tìm thương hiệu theo tên
     * @param ten - Tên thương hiệu
     * @return Optional thương hiệu
     */
    Optional<ThuongHieu> findByTen(String ten);

    /**
     * ✅ THÊM MỚI: Lấy thương hiệu có trong danh mục kèm số lượng sản phẩm
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
     * ✅ THÊM MỚI: Lấy tất cả thương hiệu kèm số lượng sản phẩm
     * Dùng cho trang chủ hoặc admin
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
     * ✅ THÊM MỚI: Lấy thương hiệu có sản phẩm (không lấy thương hiệu rỗng)
     */
    @Query("SELECT DISTINCT th FROM ThuongHieu th " +
            "INNER JOIN SanPham sp ON th.thuongHieuId = sp.thuongHieu.thuongHieuId " +
            "WHERE th.trangThai = 1 " +
            "AND sp.trangThai = 1 " +
            "ORDER BY th.ten ASC")
    List<ThuongHieu> findBrandsWithProducts();

    /**
     * ✅ THÊM MỚI: Đếm số sản phẩm theo thương hiệu
     */
    @Query("SELECT COUNT(sp) FROM SanPham sp " +
            "WHERE sp.thuongHieu.thuongHieuId = :brandId " +
            "AND sp.trangThai = 1")
    long countProductsByBrand(@Param("brandId") Integer brandId);

    /**
     * ✅ THÊM MỚI: Tìm thương hiệu theo tên (search, case-insensitive)
     */
    @Query("SELECT th FROM ThuongHieu th " +
            "WHERE LOWER(th.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND th.trangThai = 1")
    List<ThuongHieu> searchBrands(@Param("keyword") String keyword);

    /**
     * ✅ THÊM MỚI: Lấy top thương hiệu có nhiều sản phẩm nhất
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