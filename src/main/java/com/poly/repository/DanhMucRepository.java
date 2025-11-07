package com.poly.repository;

import com.poly.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository để truy vấn dữ liệu danh mục
 *
 * @author Nhóm 132
 */
@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {

    /**
     * Tìm danh mục theo trạng thái
     * @param trangThai - 1: active, 0: inactive
     * @return List danh mục
     */
    List<DanhMuc> findByTrangThai(int trangThai);

    /**
     * Tìm danh mục theo trạng thái và sắp xếp theo tên
     * @param trangThai - 1: active, 0: inactive
     * @return List danh mục đã sắp xếp
     */
    List<DanhMuc> findByTrangThaiOrderByTenAsc(int trangThai);

    /**
     * Tìm danh mục theo ID và trạng thái
     * @param danhMucId - ID danh mục
     * @param trangThai - 1: active, 0: inactive
     * @return Optional danh mục
     */
    Optional<DanhMuc> findByDanhMucIdAndTrangThai(Integer danhMucId, int trangThai);
}