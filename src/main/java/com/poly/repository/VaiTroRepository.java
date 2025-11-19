package com.poly.repository;

import com.poly.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, Integer> {

    /**
     * Tìm vai trò theo tên
     */
    Optional<VaiTro> findByTenVaiTro(String tenVaiTro);

    /**
     * Kiểm tra tên vai trò đã tồn tại chưa
     */
    boolean existsByTenVaiTro(String tenVaiTro);

    /**
     * Kiểm tra tên vai trò đã tồn tại chưa (trừ ID hiện tại - dùng cho update)
     */
    @Query("SELECT COUNT(v) > 0 FROM VaiTro v WHERE v.tenVaiTro = :tenVaiTro AND v.vaiTroId != :vaiTroId")
    boolean existsByTenVaiTroAndNotId(@Param("tenVaiTro") String tenVaiTro, @Param("vaiTroId") Integer vaiTroId);

    /**
     * Đếm số lượng nhân viên theo vai trò
     */
    @Query("SELECT COUNT(nv) FROM NhanVien nv WHERE nv.vaiTro.vaiTroId = :vaiTroId")
    Long countNhanVienByVaiTroId(@Param("vaiTroId") Integer vaiTroId);
}