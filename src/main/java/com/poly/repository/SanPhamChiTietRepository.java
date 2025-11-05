package com.poly.repository;

import com.poly.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {

    /**
     * Tìm biến thể theo sản phẩm
     */
    List<SanPhamChiTiet> findBySanPhamSanPhamId(Integer sanPhamId);

    /**
     * Tìm biến thể theo sản phẩm và trạng thái
     */
    List<SanPhamChiTiet> findBySanPhamSanPhamIdAndTrangThai(Integer sanPhamId, Byte trangThai);

    /**
     * Tìm tất cả variants của một sản phẩm (có JOIN để lấy luôn thông tin màu sắc và kích thước)
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "LEFT JOIN FETCH spct.mauSac ms " +
            "LEFT JOIN FETCH spct.kichThuoc kt " +
            "LEFT JOIN FETCH spct.sanPham sp " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.trangThai = 1 " +
            "ORDER BY ms.ten ASC, kt.ten ASC")
    List<SanPhamChiTiet> findBySanPhamIdWithDetails(@Param("sanPhamId") Integer sanPhamId);

    /**
     * Tìm variant cụ thể theo sản phẩm, màu sắc và kích thước
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "LEFT JOIN FETCH spct.mauSac " +
            "LEFT JOIN FETCH spct.kichThuoc " +
            "LEFT JOIN FETCH spct.sanPham " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.mauSac.mauSacId = :mauSacId " +
            "AND spct.kichThuoc.kichThuocId = :kichThuocId " +
            "AND spct.trangThai = 1")
    Optional<SanPhamChiTiet> findByProductColorSize(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("mauSacId") Integer mauSacId,
            @Param("kichThuocId") Integer kichThuocId);

    /**
     * Tìm biến thể theo màu sắc và kích thước (JPA method)
     */
    Optional<SanPhamChiTiet> findBySanPhamSanPhamIdAndMauSacMauSacIdAndKichThuocKichThuocId(
            Integer sanPhamId, Integer mauSacId, Integer kichThuocId);

    /**
     * Tìm biến thể theo SKU
     */
    Optional<SanPhamChiTiet> findBySku(String sku);

    /**
     * Tìm biến thể theo Barcode
     */
    Optional<SanPhamChiTiet> findByBarcode(String barcode);

    /**
     * Lấy biến thể sắp hết hàng
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "LEFT JOIN FETCH spct.sanPham sp " +
            "LEFT JOIN FETCH spct.mauSac " +
            "LEFT JOIN FETCH spct.kichThuoc " +
            "WHERE spct.trangThai = 1 " +
            "AND spct.soLuongTon > 0 " +
            "AND spct.soLuongTon <= :threshold " +
            "ORDER BY spct.soLuongTon ASC")
    List<SanPhamChiTiet> findLowStockVariants(@Param("threshold") int threshold);

    /**
     * Lấy biến thể hết hàng
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "LEFT JOIN FETCH spct.sanPham sp " +
            "LEFT JOIN FETCH spct.mauSac " +
            "LEFT JOIN FETCH spct.kichThuoc " +
            "WHERE spct.trangThai = 1 " +
            "AND spct.soLuongTon = 0")
    List<SanPhamChiTiet> findOutOfStockVariants();

    /**
     * Lấy biến thể còn hàng của sản phẩm
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.trangThai = 1 " +
            "AND spct.soLuongTon > 0 " +
            "ORDER BY spct.soLuongTon DESC")
    List<SanPhamChiTiet> findAvailableVariantsBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    /**
     * Lấy danh sách màu sắc có sẵn của sản phẩm
     */
    @Query("SELECT DISTINCT spct.mauSac FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.trangThai = 1 " +
            "AND spct.soLuongTon > 0 " +
            "ORDER BY spct.mauSac.ten ASC")
    List<com.poly.entity.MauSac> findAvailableColorsBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    /**
     * Lấy danh sách kích thước có sẵn của sản phẩm theo màu
     */
    @Query("SELECT DISTINCT spct.kichThuoc FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.mauSac.mauSacId = :mauSacId " +
            "AND spct.trangThai = 1 " +
            "AND spct.soLuongTon > 0 " +
            "ORDER BY spct.kichThuoc.ten ASC")
    List<com.poly.entity.KichThuoc> findAvailableSizesByProductAndColor(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("mauSacId") Integer mauSacId);

    /**
     * Đếm số lượng biến thể theo sản phẩm
     */
    long countBySanPhamSanPhamId(Integer sanPhamId);

    /**
     * Đếm số lượng biến thể còn hàng theo sản phẩm
     */
    @Query("SELECT COUNT(spct) FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.trangThai = 1 " +
            "AND spct.soLuongTon > 0")
    long countAvailableVariantsBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    /**
     * Kiểm tra SKU có tồn tại không
     */
    boolean existsBySku(String sku);

    /**
     * Kiểm tra Barcode có tồn tại không
     */
    boolean existsByBarcode(String barcode);

    /**
     * Kiểm tra biến thể có tồn tại không
     */
    @Query("SELECT CASE WHEN COUNT(spct) > 0 THEN true ELSE false END " +
            "FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.mauSac.mauSacId = :mauSacId " +
            "AND spct.kichThuoc.kichThuocId = :kichThuocId")
    boolean existsByProductColorSize(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("mauSacId") Integer mauSacId,
            @Param("kichThuocId") Integer kichThuocId);

    /**
     * Lấy giá thấp nhất của sản phẩm
     */
    @Query("SELECT MIN(spct.giaBan) FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.trangThai = 1")
    java.math.BigDecimal findMinPriceBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    /**
     * Lấy giá cao nhất của sản phẩm
     */
    @Query("SELECT MAX(spct.giaBan) FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.trangThai = 1")
    java.math.BigDecimal findMaxPriceBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    /**
     * Tính tổng số lượng tồn kho của sản phẩm
     */
    @Query("SELECT SUM(spct.soLuongTon) FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.trangThai = 1")
    Integer sumSoLuongTonBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    /**
     * Lấy biến thể có giá thấp nhất của sản phẩm
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.trangThai = 1 " +
            "ORDER BY spct.giaBan ASC")
    List<SanPhamChiTiet> findCheapestVariantBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    /**
     * Lấy biến thể đang giảm giá
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "LEFT JOIN FETCH spct.sanPham sp " +
            "LEFT JOIN FETCH spct.mauSac " +
            "LEFT JOIN FETCH spct.kichThuoc " +
            "WHERE spct.trangThai = 1 " +
            "AND spct.giaGoc IS NOT NULL " +
            "AND spct.giaBan < spct.giaGoc " +
            "ORDER BY (spct.giaGoc - spct.giaBan) DESC")
    List<SanPhamChiTiet> findDiscountedVariants();

    /**
     * Tìm biến thể theo danh sách ID
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "LEFT JOIN FETCH spct.sanPham sp " +
            "LEFT JOIN FETCH spct.mauSac " +
            "LEFT JOIN FETCH spct.kichThuoc " +
            "WHERE spct.variantId IN :ids")
    List<SanPhamChiTiet> findByIdIn(@Param("ids") List<Integer> ids);

    /**
     * Lấy biến thể theo màu sắc
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.mauSac.mauSacId = :mauSacId " +
            "AND spct.trangThai = 1 " +
            "ORDER BY spct.kichThuoc.ten ASC")
    List<SanPhamChiTiet> findBySanPhamIdAndMauSacId(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("mauSacId") Integer mauSacId);

    /**
     * Lấy biến thể theo kích thước
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.sanPhamId = :sanPhamId " +
            "AND spct.kichThuoc.kichThuocId = :kichThuocId " +
            "AND spct.trangThai = 1 " +
            "ORDER BY spct.mauSac.ten ASC")
    List<SanPhamChiTiet> findBySanPhamIdAndKichThuocId(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("kichThuocId") Integer kichThuocId);

    /**
     * Tìm biến thể với thông tin đầy đủ theo ID
     */
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "LEFT JOIN FETCH spct.sanPham sp " +
            "LEFT JOIN FETCH sp.danhMuc " +
            "LEFT JOIN FETCH sp.thuongHieu " +
            "LEFT JOIN FETCH spct.mauSac " +
            "LEFT JOIN FETCH spct.kichThuoc " +
            "WHERE spct.variantId = :id")
    Optional<SanPhamChiTiet> findByIdWithFullDetails(@Param("id") Integer id);
}
