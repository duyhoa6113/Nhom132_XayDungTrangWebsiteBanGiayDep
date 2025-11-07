package com.poly.repository;

import com.poly.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository để truy vấn dữ liệu thương hiệu
 *
 * @author Nhóm 132
 */
@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Integer> {

    /**
     * Tìm thương hiệu theo trạng thái
     * @param trangThai - 1: active, 0: inactive
     * @return List thương hiệu
     */
    List<ThuongHieu> findByTrangThai(int trangThai);

    /**
     * Tìm thương hiệu theo trạng thái và sắp xếp theo tên
     * @param trangThai - 1: active, 0: inactive
     * @return List thương hiệu đã sắp xếp
     */
    List<ThuongHieu> findByTrangThaiOrderByTenAsc(int trangThai);

    /**
     * Tìm thương hiệu theo ID và trạng thái
     * @param thuongHieuId - ID thương hiệu
     * @param trangThai - 1: active, 0: inactive
     * @return Optional thương hiệu
     */
    Optional<ThuongHieu> findByThuongHieuIdAndTrangThai(Integer thuongHieuId, int trangThai);

    /**
     * Tìm thương hiệu theo tên
     * @param ten - Tên thương hiệu
     * @return Optional thương hiệu
     */
    Optional<ThuongHieu> findByTen(String ten);
}