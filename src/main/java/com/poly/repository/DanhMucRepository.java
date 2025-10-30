package com.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.poly.entity.DanhMuc;

import java.util.List;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {

    // Lấy tất cả danh mục đang hoạt động
    List<DanhMuc> findByTrangThaiOrderByTenAsc(Byte trangThai);

    // Đếm số sản phẩm trong mỗi danh mục
    @Query("""
        SELECT dm.danhMucId, dm.ten, COUNT(sp.sanPhamId)
        FROM DanhMuc dm
        LEFT JOIN dm.sanPhamList sp
        WHERE dm.trangThai = 1
        GROUP BY dm.danhMucId, dm.ten
        ORDER BY dm.ten ASC
    """)
    List<Object[]> countProductsByCategory();
}