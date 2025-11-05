package com.poly.repository;

import com.poly.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Integer> {

    /**
     * Lấy tất cả thương hiệu đang hoạt động
     */
    List<ThuongHieu> findByTrangThai(Byte trangThai);

    /**
     * Lấy thương hiệu kèm số lượng sản phẩm
     */
    @Query("SELECT th, COUNT(sp.sanPhamId) as soLuong " +
            "FROM ThuongHieu th " +
            "LEFT JOIN SanPham sp ON sp.thuongHieu.thuongHieuId = th.thuongHieuId " +
            "WHERE th.trangThai = 1 " +
            "GROUP BY th.thuongHieuId, th.ten, th.moTa, th.trangThai " +
            "ORDER BY th.ten ASC")
    List<Object[]> findBrandsWithProductCount();

    /**
     * Tìm thương hiệu theo tên
     */
    ThuongHieu findByTen(String ten);

    /**
     * Kiểm tra thương hiệu có tồn tại không
     */
    boolean existsByTen(String ten);
}