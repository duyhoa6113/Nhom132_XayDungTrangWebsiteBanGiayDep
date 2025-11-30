package com.poly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.entity.HoaDon;
import com.poly.entity.KhachHang;

/**
 * HoaDonRepository - Repository cho entity HoaDon
 */
@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    /**
     * Tìm tất cả đơn hàng của khách hàng (có phân trang)
     */
    Page<HoaDon> findByKhachHang(KhachHang khachHang, Pageable pageable);

    /**
     * Tìm đơn hàng của khách hàng theo trạng thái (có phân trang)
     */
    Page<HoaDon> findByKhachHangAndTrangThai(KhachHang khachHang, String trangThai, Pageable pageable);

    /**
     * Tìm tất cả đơn hàng của khách hàng (không phân trang)
     */
    List<HoaDon> findByKhachHangOrderByCreatedAtDesc(KhachHang khachHang);

    /**
     * Tìm đơn hàng theo mã hóa đơn
     */
    Optional<HoaDon> findByMaHoaDon(String maHoaDon);

    /**
     * Đếm số đơn hàng của khách hàng theo trạng thái
     */
    Long countByKhachHangAndTrangThai(KhachHang khachHang, String trangThai);

    /**
     * Đếm tổng số đơn hàng của khách hàng
     */
    Long countByKhachHang(KhachHang khachHang);

    /**
     * Tìm đơn hàng của khách hàng theo khoảng thời gian
     */
    @Query("SELECT h FROM HoaDon h WHERE h.khachHang = :khachHang " +
            "AND h.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY h.createdAt DESC")
    List<HoaDon> findByKhachHangAndDateRange(
            @Param("khachHang") KhachHang khachHang,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );

    /**
     * Tìm đơn hàng theo trạng thái (không phân trang) - cho admin
     */
    List<HoaDon> findByTrangThaiOrderByCreatedAtDesc(String trangThai);

    /**
     * Tìm đơn hàng theo trạng thái (có phân trang) - cho admin
     */
    Page<HoaDon> findByTrangThai(String trangThai, Pageable pageable);

    /**
     * Đếm số đơn hàng theo trạng thái - cho admin
     */
    Long countByTrangThai(String trangThai);

    /**
     * Tìm tất cả đơn hàng (có phân trang) - cho admin
     */
    Page<HoaDon> findAll(Pageable pageable);

    // ========== DASHBOARD QUERIES ==========

    /**
     * Tính tổng doanh thu của tháng hiện tại
     */
    @Query(value = "SELECT COALESCE(SUM(TongThanhToan), 0) FROM HoaDon " +
            "WHERE YEAR(CreatedAt) = YEAR(GETDATE()) " +
            "AND MONTH(CreatedAt) = MONTH(GETDATE())", nativeQuery = true)
    java.math.BigDecimal getTotalRevenueCurrentMonth();

    /**
     * Tính tổng doanh thu của tháng trước
     */
    @Query(value = "SELECT COALESCE(SUM(TongThanhToan), 0) FROM HoaDon " +
            "WHERE YEAR(CreatedAt) = YEAR(DATEADD(MONTH, -1, GETDATE())) " +
            "AND MONTH(CreatedAt) = MONTH(DATEADD(MONTH, -1, GETDATE()))", nativeQuery = true)
    java.math.BigDecimal getTotalRevenuePreviousMonth();

    /**
     * Đếm tổng số đơn hàng
     */
    @Query("SELECT COUNT(h) FROM HoaDon h")
    Long countAllOrders();

    /**
     * Đếm số đơn hàng trong tháng hiện tại
     */
    @Query(value = "SELECT COUNT(*) FROM HoaDon " +
            "WHERE YEAR(CreatedAt) = YEAR(GETDATE()) " +
            "AND MONTH(CreatedAt) = MONTH(GETDATE())", nativeQuery = true)
    Long countOrdersCurrentMonth();

    /**
     * Lấy 5 đơn hàng gần đây nhất (với khách hàng được fetch)
     */
    @Query("SELECT h FROM HoaDon h LEFT JOIN FETCH h.khachHang ORDER BY h.createdAt DESC")
    List<HoaDon> findTop5ByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Tính doanh thu theo tháng (12 tháng gần nhất)
     * Returns: Object[] với [year, month, totalRevenue]
     */
    @Query(value = """
        SELECT 
            YEAR(h.CreatedAt) as year,
            MONTH(h.CreatedAt) as month,
            COALESCE(SUM(h.TongThanhToan), 0) as totalRevenue
        FROM HoaDon h
        WHERE h.CreatedAt >= DATEADD(MONTH, -12, GETDATE())
        GROUP BY YEAR(h.CreatedAt), MONTH(h.CreatedAt)
        ORDER BY YEAR(h.CreatedAt), MONTH(h.CreatedAt)
        """, nativeQuery = true)
    List<Object[]> getRevenueByMonthLast12Months();
}