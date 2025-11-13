package com.poly.repository;

import com.poly.entity.GioHang;
import com.poly.entity.KhachHang;
import com.poly.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    List<GioHang> findByKhachHang(KhachHang khachHang);

    List<GioHang> findByKhachHangOrderByCreatedAtDesc(KhachHang khachHang);

    Optional<GioHang> findByKhachHangAndVariant(KhachHang khachHang, SanPhamChiTiet variant);

    @Query("SELECT COUNT(g) FROM GioHang g WHERE g.khachHang = :khachHang")
    Long countByKhachHang(KhachHang khachHang);

    @Query("SELECT SUM(g.soLuong) FROM GioHang g WHERE g.khachHang = :khachHang")
    Integer sumQuantityByKhachHang(KhachHang khachHang);

    @Modifying
    @Transactional
    void deleteByKhachHang(KhachHang khachHang);

    @Modifying
    @Transactional
    @Query("DELETE FROM GioHang g WHERE g.khachHang = :khachHang AND g.gioHangId IN :ids")
    void deleteByKhachHangAndGioHangIdIn(KhachHang khachHang, List<Integer> ids);

    @Query("SELECT g FROM GioHang g WHERE g.khachHang = :khachHang AND g.gioHangId IN :ids")
    List<GioHang> findByKhachHangAndGioHangIdIn(KhachHang khachHang, List<Integer> ids);
}