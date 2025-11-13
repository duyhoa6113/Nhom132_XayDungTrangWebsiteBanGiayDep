package com.poly.repository;

import com.poly.entity.DiaChi;
import com.poly.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, Integer> {

    List<DiaChi> findByKhachHang(KhachHang khachHang);

    List<DiaChi> findByKhachHangOrderByMacDinhDesc(KhachHang khachHang);

    Optional<DiaChi> findByKhachHangAndMacDinh(KhachHang khachHang, Boolean macDinh);

    @Query("SELECT d FROM DiaChi d WHERE d.khachHang = :khachHang AND d.macDinh = true")
    Optional<DiaChi> findDefaultAddress(KhachHang khachHang);
}