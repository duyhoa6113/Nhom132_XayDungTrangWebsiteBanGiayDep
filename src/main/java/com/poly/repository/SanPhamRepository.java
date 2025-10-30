package com.poly.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.dto.ProductHomeDTO;
import com.poly.entity.SanPham;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

	// Lấy danh sách sản phẩm cho trang chủ với thông tin cơ bản
	@Query("""
			    SELECT new com.poly.dto.ProductHomeDTO(
			        sp.sanPhamId,
			        sp.ten,
			        sp.moTa,
			        dm.ten,
			        th.ten,
			        MIN(spct.giaBan),
			        MAX(spct.giaBan),
			        MIN(spct.giaGoc),
			        MIN(spct.hinhAnh),
			        SUM(spct.soLuongTon),
			        dm.danhMucId
			    )
			    FROM SanPham sp
			    LEFT JOIN sp.danhMuc dm
			    LEFT JOIN sp.thuongHieu th
			    LEFT JOIN sp.chiTietList spct
			    WHERE sp.trangThai = 1
			    AND spct.trangThai = 1
			    AND spct.soLuongTon > 0
			    GROUP BY sp.sanPhamId, sp.ten, sp.moTa, dm.ten, th.ten, dm.danhMucId
			    ORDER BY sp.sanPhamId DESC
			""")
	Page<ProductHomeDTO> findAllForHomePage(Pageable pageable);

	// Lấy sản phẩm theo danh mục
	@Query("""
			    SELECT new com.poly.dto.ProductHomeDTO(
			        sp.sanPhamId,
			        sp.ten,
			        sp.moTa,
			        dm.ten,
			        th.ten,
			        MIN(spct.giaBan),
			        MAX(spct.giaBan),
			        MIN(spct.giaGoc),
			        MIN(spct.hinhAnh),
			        SUM(spct.soLuongTon),
			        dm.danhMucId
			    )
			    FROM SanPham sp
			    LEFT JOIN sp.danhMuc dm
			    LEFT JOIN sp.thuongHieu th
			    LEFT JOIN sp.chiTietList spct
			    WHERE sp.trangThai = 1
			    AND spct.trangThai = 1
			    AND spct.soLuongTon > 0
			    AND dm.danhMucId = :danhMucId
			    GROUP BY sp.sanPhamId, sp.ten, sp.moTa, dm.ten, th.ten, dm.danhMucId
			    ORDER BY sp.sanPhamId DESC
			""")
	Page<ProductHomeDTO> findByDanhMucId(@Param("danhMucId") Integer danhMucId, Pageable pageable);

	// Tìm kiếm sản phẩm theo tên hoặc mô tả
	@Query("""
			    SELECT new com.poly.dto.ProductHomeDTO(
			        sp.sanPhamId,
			        sp.ten,
			        sp.moTa,
			        dm.ten,
			        th.ten,
			        MIN(spct.giaBan),
			        MAX(spct.giaBan),
			        MIN(spct.giaGoc),
			        MIN(spct.hinhAnh),
			        SUM(spct.soLuongTon),
			        dm.danhMucId
			    )
			    FROM SanPham sp
			    LEFT JOIN sp.danhMuc dm
			    LEFT JOIN sp.thuongHieu th
			    LEFT JOIN sp.chiTietList spct
			    WHERE sp.trangThai = 1
			    AND spct.trangThai = 1
			    AND spct.soLuongTon > 0
			    AND (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :keyword, '%'))
			         OR LOWER(sp.moTa) LIKE LOWER(CONCAT('%', :keyword, '%'))
			         OR LOWER(th.ten) LIKE LOWER(CONCAT('%', :keyword, '%')))
			    GROUP BY sp.sanPhamId, sp.ten, sp.moTa, dm.ten, th.ten, dm.danhMucId
			    ORDER BY sp.sanPhamId DESC
			""")
	Page<ProductHomeDTO> searchProducts(@Param("keyword") String keyword, Pageable pageable);

	// Lấy sản phẩm nổi bật (sắp xếp theo số lượng bán)
	@Query("""
			    SELECT new com.poly.dto.ProductHomeDTO(
			        sp.sanPhamId,
			        sp.ten,
			        sp.moTa,
			        dm.ten,
			        th.ten,
			        MIN(spct.giaBan),
			        MAX(spct.giaBan),
			        MIN(spct.giaGoc),
			        MIN(spct.hinhAnh),
			        SUM(spct.soLuongTon),
			        dm.danhMucId
			    )
			    FROM SanPham sp
			    LEFT JOIN sp.danhMuc dm
			    LEFT JOIN sp.thuongHieu th
			    LEFT JOIN sp.chiTietList spct
			    WHERE sp.trangThai = 1
			    AND spct.trangThai = 1
			    AND spct.soLuongTon > 0
			    GROUP BY sp.sanPhamId, sp.ten, sp.moTa, dm.ten, th.ten, dm.danhMucId
			    ORDER BY SUM(spct.soLuongTon) DESC
			""")
	List<ProductHomeDTO> findFeaturedProducts(Pageable pageable);
}