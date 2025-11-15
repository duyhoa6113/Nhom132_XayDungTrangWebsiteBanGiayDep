package com.poly.repository;

import com.poly.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {

    /**
     * Tìm chi tiết đơn hàng theo hóa đơn ID
     */
    @Query("SELECT hd FROM HoaDonChiTiet hd WHERE hd.hoaDon.hoaDonId = :hoaDonId")
    List<HoaDonChiTiet> findByHoaDonId(@Param("hoaDonId") Integer hoaDonId);
}