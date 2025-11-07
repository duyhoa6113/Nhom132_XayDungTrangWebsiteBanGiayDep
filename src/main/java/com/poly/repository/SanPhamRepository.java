package com.poly.repository;

import com.poly.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository để truy vấn dữ liệu sản phẩm
 *
 * @author Nhóm 132
 */
@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    /**
     * Lấy sản phẩm nổi bật - mới nhất, đang active
     * @param pageable - phân trang
     * @return List sản phẩm
     */
    @Query("SELECT sp FROM SanPham sp " +
            "WHERE sp.trangThai = 1 " +
            "ORDER BY sp.createdAt DESC")
    List<SanPham> findFeaturedProducts(Pageable pageable);

    /**
     * Lấy sản phẩm có giảm giá cao nhất
     * @param pageable - phân trang
     * @return List sản phẩm
     */
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
            "JOIN sp.variants v " +
            "WHERE sp.trangThai = 1 AND v.giaGoc IS NOT NULL " +
            "AND v.giaGoc > v.giaBan " +
            "ORDER BY (v.giaGoc - v.giaBan) / v.giaGoc DESC")
    List<SanPham> findTopDiscountedProducts(Pageable pageable);

    /**
     * Lấy sản phẩm mới nhất
     * @param pageable - phân trang
     * @return List sản phẩm
     */
    @Query("SELECT sp FROM SanPham sp " +
            "WHERE sp.trangThai = 1 " +
            "ORDER BY sp.createdAt DESC")
    List<SanPham> findNewestProducts(Pageable pageable);

    /**
     * Tìm sản phẩm theo trạng thái
     * @param trangThai - 1: active, 0: inactive
     * @param pageable - phân trang
     * @return Page sản phẩm
     */
    Page<SanPham> findByTrangThai(int trangThai, Pageable pageable);

    /**
     * Tìm sản phẩm theo danh mục và trạng thái
     * @param danhMucId - ID danh mục
     * @param trangThai - 1: active, 0: inactive
     * @param pageable - phân trang
     * @return Page sản phẩm
     */
    Page<SanPham> findByDanhMuc_DanhMucIdAndTrangThai(Integer danhMucId, int trangThai, Pageable pageable);

    /**
     * Tìm kiếm sản phẩm theo từ khóa (tên sản phẩm hoặc thương hiệu)
     * @param keyword - từ khóa tìm kiếm
     * @param pageable - phân trang
     * @return Page sản phẩm
     */
    @Query("SELECT sp FROM SanPham sp " +
            "LEFT JOIN sp.thuongHieu th " +
            "WHERE sp.trangThai = 1 " +
            "AND (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(th.ten) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SanPham> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Tìm sản phẩm theo ID và trạng thái
     * @param sanPhamId - ID sản phẩm
     * @param trangThai - 1: active, 0: inactive
     * @return Optional sản phẩm
     */
    Optional<SanPham> findBySanPhamIdAndTrangThai(Integer sanPhamId, int trangThai);
}