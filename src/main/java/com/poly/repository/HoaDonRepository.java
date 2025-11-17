package com.poly.repository;

import com.poly.entity.HoaDon;
import com.poly.entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
     * Tìm tất cả đơn hàng (có phân trang) - cho admin
     */
    Page<HoaDon> findAll(Pageable pageable);
}