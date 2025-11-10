package com.poly.repository;

import com.poly.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends PagingAndSortingRepository<SanPham, Integer> {

    // Sort giá tăng dần
    @Query("""
    SELECT sp FROM SanPham sp
    JOIN sp.variants v
    WHERE sp.danhMuc.danhMucId = :categoryId
      AND v.trangThai = 1
    GROUP BY sp
    ORDER BY MIN(v.giaBan) ASC
""")
    Page<SanPham> findByCategoryOrderByGiaBanAsc(@Param("categoryId") Integer categoryId, Pageable pageable);

    // Sort giá giảm dần
    @Query("""
    SELECT sp FROM SanPham sp
    JOIN sp.variants v
    WHERE sp.danhMuc.danhMucId = :categoryId
      AND v.trangThai = 1
    GROUP BY sp
    ORDER BY MIN(v.giaBan) DESC
""")
    Page<SanPham> findByCategoryOrderByGiaBanDesc(@Param("categoryId") Integer categoryId, Pageable pageable);
}
