package com.poly.repository;

import com.poly.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {

    Optional<KhuyenMai> findByMa(String ma);

    @Query("SELECT k FROM KhuyenMai k WHERE k.trangThai = 1 " +
            "AND k.ngayBatDau <= :currentDate " +
            "AND k.ngayKetThuc >= :currentDate " +
            "AND k.soLuong > 0")
    List<KhuyenMai> findActiveVouchers(LocalDate currentDate);

    @Query("SELECT k FROM KhuyenMai k WHERE k.ma = :ma " +
            "AND k.trangThai = 1 " +
            "AND k.ngayBatDau <= :currentDate " +
            "AND k.ngayKetThuc >= :currentDate " +
            "AND k.soLuong > 0")
    Optional<KhuyenMai> findValidVoucherByCode(String ma, LocalDate currentDate);
}