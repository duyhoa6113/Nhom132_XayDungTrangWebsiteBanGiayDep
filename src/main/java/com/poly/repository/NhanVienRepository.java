package com.poly.repository;

import com.poly.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    /**
     * Tìm nhân viên theo email
     */
    Optional<NhanVien> findByEmail(String email);

    /**
     * Kiểm tra email đã tồn tại chưa
     */
    boolean existsByEmail(String email);

    /**
     * Tìm nhân viên theo vai trò
     */
    @Query("SELECT nv FROM NhanVien nv WHERE nv.vaiTro.vaiTroId = :vaiTroId")
    List<NhanVien> findByVaiTroId(@Param("vaiTroId") Integer vaiTroId);

    /**
     * Tìm nhân viên theo trạng thái
     */
    List<NhanVien> findByTrangThai(Integer trangThai);

    /**
     * Tìm nhân viên theo vai trò và trạng thái
     */
    @Query("SELECT nv FROM NhanVien nv WHERE nv.vaiTro.vaiTroId = :vaiTroId AND nv.trangThai = :trangThai")
    List<NhanVien> findByVaiTroIdAndTrangThai(@Param("vaiTroId") Integer vaiTroId, @Param("trangThai") Integer trangThai);

    /**
     * Tìm kiếm nhân viên theo từ khóa
     */
    @Query("SELECT nv FROM NhanVien nv WHERE " +
            "LOWER(nv.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(nv.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(nv.sdt) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<NhanVien> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Đếm số nhân viên theo vai trò
     */
    @Query("SELECT COUNT(nv) FROM NhanVien nv WHERE nv.vaiTro.vaiTroId = :vaiTroId")
    Long countByVaiTroId(@Param("vaiTroId") Integer vaiTroId);
}