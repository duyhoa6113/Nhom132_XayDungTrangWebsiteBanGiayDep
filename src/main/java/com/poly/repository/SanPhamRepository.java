package com.poly.repository;

import com.poly.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;  // ✅ THÊM IMPORT
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
public interface SanPhamRepository extends JpaRepository<SanPham, Integer>,
        JpaSpecificationExecutor<SanPham> {  // ✅ THÊM INTERFACE NÀY ĐỂ DÙNG SPECIFICATION

    Page<SanPham> findByTrangThai(Integer trangThai, Pageable pageable);
    Page<SanPham> findByDanhMuc_DanhMucIdAndTrangThai(Integer danhMucId, Integer trangThai, Pageable pageable);

    /**
     * Lấy sản phẩm cùng danh mục, loại trừ sản phẩm hiện tại
     * Dùng cho: Sản phẩm liên quan
     */
    Page<SanPham> findByDanhMuc_DanhMucIdAndSanPhamIdNotAndTrangThai(
            Integer danhMucId,
            Integer excludeId,
            Integer trangThai,
            Pageable pageable
    );

    /**
     * Lấy sản phẩm khác, loại trừ sản phẩm hiện tại
     * Dùng khi không đủ sản phẩm cùng danh mục
     */
    Page<SanPham> findBySanPhamIdNotAndTrangThai(
            Integer excludeId,
            Integer trangThai,
            Pageable pageable
    );

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


    // Lấy sản phẩm bán chạy (giả lập - lấy random)
    @Query(value = "SELECT TOP(?1) * FROM SanPham WHERE TrangThai = 1 ORDER BY NEWID()",
            nativeQuery = true)
    List<SanPham> findRandomProducts(int limit);

    /**
     * Đếm số sản phẩm theo danh mục và trạng thái - FIXED
     */
    @Query("SELECT COUNT(sp) FROM SanPham sp WHERE sp.danhMuc.danhMucId = :danhMucId AND sp.trangThai = :trangThai")
    long countByDanhMucIdAndTrangThai(@Param("danhMucId") Integer danhMucId,
                                      @Param("trangThai") int trangThai);

    /**
     * Tìm sản phẩm theo danh mục
     */
    @Query("SELECT sp FROM SanPham sp WHERE sp.danhMuc.danhMucId = :categoryId AND sp.trangThai = 1")
    List<SanPham> findProductsByCategory(@Param("categoryId") Integer categoryId);

    /**
     * ✅ THÊM MỚI: Tìm sản phẩm theo danh mục với phân trang
     * Dùng cho trang category
     */
    @Query("SELECT sp FROM SanPham sp WHERE sp.danhMuc.danhMucId = :categoryId AND sp.trangThai = 1")
    Page<SanPham> findProductsByCategoryWithPage(@Param("categoryId") Integer categoryId, Pageable pageable);

    /**
     * ✅ THÊM MỚI: Tìm sản phẩm theo thương hiệu
     * Dùng cho filter thương hiệu
     */
    @Query("SELECT sp FROM SanPham sp WHERE sp.thuongHieu.thuongHieuId = :brandId AND sp.trangThai = 1")
    Page<SanPham> findProductsByBrand(@Param("brandId") Integer brandId, Pageable pageable);

    /**
     * ✅ THÊM MỚI: Tìm sản phẩm theo danh mục và thương hiệu
     * Dùng khi filter cả 2
     */
    @Query("SELECT sp FROM SanPham sp " +
            "WHERE sp.danhMuc.danhMucId = :categoryId " +
            "AND sp.thuongHieu.thuongHieuId = :brandId " +
            "AND sp.trangThai = 1")
    Page<SanPham> findProductsByCategoryAndBrand(
            @Param("categoryId") Integer categoryId,
            @Param("brandId") Integer brandId,
            Pageable pageable
    );

    /**
     * ✅ THÊM MỚI: Lấy sản phẩm theo danh sách thương hiệu
     */
    @Query("SELECT sp FROM SanPham sp " +
            "WHERE sp.danhMuc.danhMucId = :categoryId " +
            "AND sp.thuongHieu.thuongHieuId IN :brandIds " +
            "AND sp.trangThai = 1")
    Page<SanPham> findProductsByCategoryAndBrands(
            @Param("categoryId") Integer categoryId,
            @Param("brandIds") List<Integer> brandIds,
            Pageable pageable
    );

    /**
     * ✅ THÊM MỚI: Lấy sản phẩm theo chất liệu
     */
    @Query("SELECT sp FROM SanPham sp " +
            "WHERE sp.chatLieu.chatLieuId = :materialId " +
            "AND sp.trangThai = 1")
    Page<SanPham> findProductsByMaterial(@Param("materialId") Integer materialId, Pageable pageable);

    /**
     * ✅ THÊM MỚI: Đếm sản phẩm theo thương hiệu
     */
    @Query("SELECT COUNT(sp) FROM SanPham sp " +
            "WHERE sp.thuongHieu.thuongHieuId = :brandId " +
            "AND sp.trangThai = 1")
    long countByBrandId(@Param("brandId") Integer brandId);

    /**
     * ✅ THÊM MỚI: Đếm sản phẩm theo chất liệu
     */
    @Query("SELECT COUNT(sp) FROM SanPham sp " +
            "WHERE sp.chatLieu.chatLieuId = :materialId " +
            "AND sp.trangThai = 1")
    long countByMaterialId(@Param("materialId") Integer materialId);
}