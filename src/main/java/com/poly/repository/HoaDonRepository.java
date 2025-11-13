package com.poly.repository;

import com.poly.entity.HoaDon;
import com.poly.entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    Optional<HoaDon> findByMaHoaDon(String maHoaDon);

    List<HoaDon> findByKhachHangOrderByCreatedAtDesc(KhachHang khachHang);

    Page<HoaDon> findByKhachHangOrderByCreatedAtDesc(KhachHang khachHang, Pageable pageable);

    List<HoaDon> findByKhachHangAndTrangThaiOrderByCreatedAtDesc(
            KhachHang khachHang, String trangThai);

    @Query("SELECT h FROM HoaDon h WHERE h.khachHang = :khachHang " +
            "AND h.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY h.createdAt DESC")
    List<HoaDon> findByKhachHangAndDateRange(
            KhachHang khachHang, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.khachHang = :khachHang " +
            "AND h.trangThai = :trangThai")
    Long countByKhachHangAndTrangThai(KhachHang khachHang, String trangThai);
}