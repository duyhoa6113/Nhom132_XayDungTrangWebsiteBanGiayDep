package com.poly.repository;

import com.poly.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Integer> {

    Optional<ThuongHieu> findByTen(String ten);

    List<ThuongHieu> findByTrangThai(Byte trangThai);

    List<ThuongHieu> findByTenContainingIgnoreCase(String keyword);

    boolean existsByTen(String ten);
}
