package com.poly.repository;

import com.poly.entity.SanPhamChiTiet;
import com.poly.entity.SanPham;
import com.poly.entity.MauSac;
import com.poly.entity.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {

    List<SanPhamChiTiet> findBySanPham(SanPham sanPham);

    List<SanPhamChiTiet> findBySanPhamAndTrangThai(SanPham sanPham, Integer trangThai);

    Optional<SanPhamChiTiet> findBySanPhamAndMauSacAndKichThuoc(
            SanPham sanPham, MauSac mauSac, KichThuoc kichThuoc);

    @Query("SELECT v FROM SanPhamChiTiet v WHERE v.sanPham.sanPhamId = :sanPhamId " +
            "AND v.trangThai = 1")
    List<SanPhamChiTiet> findActiveBySanPhamId(Integer sanPhamId);

    @Query("SELECT DISTINCT v.mauSac FROM SanPhamChiTiet v " +
            "WHERE v.sanPham = :sanPham AND v.trangThai = 1")
    List<MauSac> findDistinctColorsBySanPham(SanPham sanPham);

    @Query("SELECT DISTINCT v.kichThuoc FROM SanPhamChiTiet v " +
            "WHERE v.sanPham = :sanPham AND v.mauSac = :mauSac AND v.trangThai = 1")
    List<KichThuoc> findDistinctSizesBySanPhamAndMauSac(SanPham sanPham, MauSac mauSac);
}