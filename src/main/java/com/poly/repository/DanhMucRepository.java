package com.poly.repository;

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
     * Kiểm tra tên danh mục đã tồn tại chưa
     * @param ten - Tên danh mục cần kiểm tra
     * @return true nếu tồn tại, false nếu chưa
     */
    boolean existsByTen(String ten);

    /**
     * Kiểm tra tên danh mục đã tồn tại (trừ ID hiện tại)
     * @param ten - Tên danh mục cần kiểm tra
     * @param danhMucId - ID danh mục hiện tại (để loại trừ khi update)
     * @return true nếu tồn tại, false nếu chưa
     */
    @Query("SELECT CASE WHEN COUNT(dm) > 0 THEN true ELSE false END " +
            "FROM DanhMuc dm WHERE dm.ten = :ten AND dm.danhMucId != :id")
    boolean existsByTenAndNotId(@Param("ten") String ten, @Param("id") Integer danhMucId);

    /**
     * Lấy tất cả danh mục kèm số lượng sản phẩm
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
     * Tìm danh mục theo ID và trạng thái (cho category page)
     * Alternative method với query rõ ràng hơn
     */
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.danhMucId = :id AND dm.trangThai = 1")
    DanhMuc findByIdAndActive(@Param("id") Integer id);

    /**
     * Đếm số sản phẩm trong danh mục
     */
    @Query("SELECT COUNT(sp) FROM SanPham sp " +
            "WHERE sp.danhMuc.danhMucId = :danhMucId " +
            "AND sp.trangThai = 1")
    long countProductsInCategory(@Param("danhMucId") Integer danhMucId);

    /**
     * Lấy danh mục có sản phẩm (không lấy danh mục rỗng)
     */
    @Query("SELECT DISTINCT dm FROM DanhMuc dm " +
            "INNER JOIN SanPham sp ON dm.danhMucId = sp.danhMuc.danhMucId " +
            "WHERE dm.trangThai = 1 " +
            "AND sp.trangThai = 1 " +
            "ORDER BY dm.ten ASC")
    List<DanhMuc> findCategoriesWithProducts();

    /**
     * Tìm danh mục theo tên (cho search)
     */
    @Query("SELECT dm FROM DanhMuc dm " +
            "WHERE LOWER(dm.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND dm.trangThai = 1")
    List<DanhMuc> searchCategories(@Param("keyword") String keyword);

    /**
     * Lấy top danh mục theo số lượng sản phẩm
     * @param limit - Số lượng danh mục cần lấy
     */
    @Query(value = """
        SELECT TOP(:limit) dm.* 
        FROM DanhMuc dm
        LEFT JOIN SanPham sp ON dm.DanhMucId = sp.DanhMucId AND sp.TrangThai = 1
        WHERE dm.TrangThai = 1
        GROUP BY dm.DanhMucId, dm.Ten, dm.MoTa, dm.TrangThai, dm.CreatedAt
        ORDER BY COUNT(sp.SanPhamId) DESC
        """, nativeQuery = true)
    List<DanhMuc> findTopCategoriesByProductCount(@Param("limit") int limit);

    /**
     * Đếm tổng số danh mục theo trạng thái
     */
    long countByTrangThai(int trangThai);

    /**
     * Lấy danh sách danh mục với thông tin chi tiết (native query)
     */
    @Query(value = """
        SELECT 
            dm.DanhMucId,
            dm.Ten,
            dm.MoTa,
            dm.TrangThai,
            dm.CreatedAt,
            COUNT(DISTINCT sp.SanPhamId) as SoLuongSanPham,
            ISNULL(SUM(sp.SoLuongDaBan), 0) as TongSanPhamDaBan
        FROM DanhMuc dm
        LEFT JOIN SanPham sp ON dm.DanhMucId = sp.DanhMucId AND sp.TrangThai = 1
        WHERE dm.TrangThai = :trangThai
        GROUP BY dm.DanhMucId, dm.Ten, dm.MoTa, dm.TrangThai, dm.CreatedAt
        ORDER BY dm.Ten ASC
        """, nativeQuery = true)
    List<Object[]> findCategoriesWithDetails(@Param("trangThai") int trangThai);
}