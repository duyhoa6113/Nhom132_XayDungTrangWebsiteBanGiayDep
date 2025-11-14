package com.poly.repository;

import com.poly.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, Integer> {

    /**
     * Lấy tất cả địa chỉ của khách hàng (mặc định lên đầu, mới nhất lên đầu)
     */
    @Query("SELECT d FROM DiaChi d WHERE d.khachHang.khachHangId = :khachHangId ORDER BY d.macDinh DESC, d.createdAt DESC")
    List<DiaChi> findByKhachHangId(@Param("khachHangId") Integer khachHangId);

    /**
     * Lấy địa chỉ mặc định của khách hàng
     */
    @Query("SELECT d FROM DiaChi d WHERE d.khachHang.khachHangId = :khachHangId AND d.macDinh = true")
    Optional<DiaChi> findDefaultAddressByKhachHangId(@Param("khachHangId") Integer khachHangId);

    /**
     * Tìm tất cả địa chỉ mặc định của khách hàng (để reset)
     */
    @Query("SELECT d FROM DiaChi d WHERE d.khachHang.khachHangId = :khachHangId AND d.macDinh = true")
    List<DiaChi> findAllDefaultAddressesByKhachHangId(@Param("khachHangId") Integer khachHangId);

    /**
     * Kiểm tra địa chỉ có thuộc về khách hàng không
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DiaChi d WHERE d.diaChiId = :diaChiId AND d.khachHang.khachHangId = :khachHangId")
    boolean existsByIdAndKhachHangId(@Param("diaChiId") Integer diaChiId, @Param("khachHangId") Integer khachHangId);

    /**
     * Đếm số địa chỉ của khách hàng
     */
    @Query("SELECT COUNT(d) FROM DiaChi d WHERE d.khachHang.khachHangId = :khachHangId")
    long countByKhachHangId(@Param("khachHangId") Integer khachHangId);
}