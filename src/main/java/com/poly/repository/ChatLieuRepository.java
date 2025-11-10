package com.poly.repository;

import com.poly.entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatLieuRepository extends JpaRepository<ChatLieu, Integer> {

    /**
     * Tìm chất liệu theo trạng thái
     */
    List<ChatLieu> findByTrangThaiOrderByTenAsc(int trangThai);

    /**
     * Lấy chất liệu có trong danh mục
     * FIXED: Chỉ cần 1 tham số categoryId
     */
    @Query("""
        SELECT DISTINCT cl
        FROM ChatLieu cl
        INNER JOIN cl.sanPhams sp
        WHERE sp.danhMuc.danhMucId = :categoryId 
          AND sp.trangThai = 1
          AND cl.trangThai = 1
        ORDER BY cl.ten ASC
    """)
    List<ChatLieu> findMaterialsByCategoryAndActive(@Param("categoryId") Integer categoryId);

    /**
     * Đếm số sản phẩm theo chất liệu
     */
    @Query("""
        SELECT COUNT(sp)
        FROM SanPham sp
        WHERE sp.chatLieu.chatLieuId = :chatLieuId
          AND sp.trangThai = 1
    """)
    Long countProductsByMaterial(@Param("chatLieuId") Integer chatLieuId);
}