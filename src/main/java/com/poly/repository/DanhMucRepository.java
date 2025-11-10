package com.poly.repository;

import com.poly.dto.CategoryWithCount;
import com.poly.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository để truy vấn dữ liệu danh mục
 *
 * @author Nhóm 132
 */
@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {

    /**
     * Tìm danh mục theo trạng thái
     * @param trangThai - 1: active, 0: inactive
     * @return List danh mục
     */
    List<DanhMuc> findByTrangThai(int trangThai);

    /**
     * Tìm danh mục theo trạng thái và sắp xếp theo tên
     * @param trangThai - 1: active, 0: inactive
     * @return List danh mục đã sắp xếp
     */
    List<DanhMuc> findByTrangThaiOrderByTenAsc(int trangThai);

    /**
     * Tìm danh mục theo ID và trạng thái
     * @param danhMucId - ID danh mục
     * @param trangThai - 1: active, 0: inactive
     * @return Optional danh mục
     */
    Optional<DanhMuc> findByDanhMucIdAndTrangThai(Integer danhMucId, int trangThai);

    /**
     * ✅ THÊM MỚI: Lấy tất cả danh mục kèm số lượng sản phẩm
     * Dùng cho sidebar và trang category
     */
    @Query("""
        SELECT dm.danhMucId as danhMucId, 
               dm.ten as ten, 
               dm.moTa as moTa,
               COUNT(sp.sanPhamId) as productCount
        FROM DanhMuc dm
        LEFT JOIN SanPham sp ON dm.danhMucId = sp.danhMuc.danhMucId AND sp.trangThai = 1
        WHERE dm.trangThai = 1
        GROUP BY dm.danhMucId, dm.ten, dm.moTa
        ORDER BY dm.ten ASC
    """)
    List<CategoryWithCount> findAllCategoriesWithProductCount();

    /**
     * ✅ THÊM MỚI: Tìm danh mục theo ID và trạng thái (cho category page)
     * Alternative method với query rõ ràng hơn
     */
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.danhMucId = :id AND dm.trangThai = 1")
    DanhMuc findByIdAndActive(@Param("id") Integer id);

    /**
     * ✅ THÊM MỚI: Đếm số sản phẩm trong danh mục
     */
    @Query("SELECT COUNT(sp) FROM SanPham sp " +
            "WHERE sp.danhMuc.danhMucId = :danhMucId " +
            "AND sp.trangThai = 1")
    long countProductsInCategory(@Param("danhMucId") Integer danhMucId);

    /**
     * ✅ THÊM MỚI: Lấy danh mục có sản phẩm (không lấy danh mục rỗng)
     */
    @Query("SELECT DISTINCT dm FROM DanhMuc dm " +
            "INNER JOIN SanPham sp ON dm.danhMucId = sp.danhMuc.danhMucId " +
            "WHERE dm.trangThai = 1 " +
            "AND sp.trangThai = 1 " +
            "ORDER BY dm.ten ASC")
    List<DanhMuc> findCategoriesWithProducts();

    /**
     * ✅ THÊM MỚI: Tìm danh mục theo tên (cho search)
     */
    @Query("SELECT dm FROM DanhMuc dm " +
            "WHERE LOWER(dm.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND dm.trangThai = 1")
    List<DanhMuc> searchCategories(@Param("keyword") String keyword);
}