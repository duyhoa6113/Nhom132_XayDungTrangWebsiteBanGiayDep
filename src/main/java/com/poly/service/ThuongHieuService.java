package com.poly.service;

import com.poly.entity.ThuongHieu;
import com.poly.repository.ThuongHieuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThuongHieuService {

    private final ThuongHieuRepository thuongHieuRepository;

    /**
     * Lấy tất cả thương hiệu
     */
    @Transactional(readOnly = true)
    public List<ThuongHieu> layTatCaThuongHieu() {
        try {
            return thuongHieuRepository.findAll();
        } catch (Exception e) {
            log.error("Lỗi khi lấy tất cả thương hiệu: ", e);
            return List.of();
        }
    }

    /**
     * Lấy tất cả thương hiệu đang hoạt động
     */
    @Transactional(readOnly = true)
    public List<ThuongHieu> layThuongHieuHoatDong() {
        try {
            return thuongHieuRepository.findByTrangThai((byte) 1);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thương hiệu hoạt động: ", e);
            return List.of();
        }
    }

    /**
     * Lấy thương hiệu theo ID
     */
    @Transactional(readOnly = true)
    public ThuongHieu layThuongHieuTheoId(Integer id) {
        try {
            return thuongHieuRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thương hiệu theo ID {}: ", id, e);
            return null;
        }
    }

    /**
     * Lấy thương hiệu theo tên
     */
    @Transactional(readOnly = true)
    public ThuongHieu layThuongHieuTheoTen(String ten) {
        try {
            return thuongHieuRepository.findByTen(ten).orElse(null);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thương hiệu theo tên {}: ", ten, e);
            return null;
        }
    }

    /**
     * Lấy thương hiệu có phân trang
     */
    @Transactional(readOnly = true)
    public Page<ThuongHieu> layThuongHieuPhanTrang(Pageable pageable) {
        try {
            return thuongHieuRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thương hiệu phân trang: ", e);
            return Page.empty();
        }
    }

    /**
     * Tìm kiếm thương hiệu theo tên
     */
    @Transactional(readOnly = true)
    public List<ThuongHieu> timKiemTheoTen(String keyword) {
        try {
            return thuongHieuRepository.findByTenContainingIgnoreCase(keyword);
        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm thương hiệu: ", e);
            return List.of();
        }
    }

    /**
     * Thêm thương hiệu mới
     */
    @Transactional
    public ThuongHieu themThuongHieu(ThuongHieu thuongHieu) {
        try {
            // Kiểm tra trùng tên
            if (thuongHieuRepository.findByTen(thuongHieu.getTen()).isPresent()) {
                log.warn("Thương hiệu với tên {} đã tồn tại", thuongHieu.getTen());
                return null;
            }

            thuongHieu.setTrangThai((byte) 1);
            thuongHieu.setCreatedAt(LocalDateTime.now());

            ThuongHieu saved = thuongHieuRepository.save(thuongHieu);
            log.info("Đã thêm thương hiệu mới: {}", saved.getTen());
            return saved;
        } catch (Exception e) {
            log.error("Lỗi khi thêm thương hiệu: ", e);
            return null;
        }
    }

    /**
     * Cập nhật thương hiệu
     */
    @Transactional
    public ThuongHieu capNhatThuongHieu(Integer id, ThuongHieu thuongHieu) {
        try {
            ThuongHieu existing = thuongHieuRepository.findById(id).orElse(null);
            if (existing == null) {
                log.warn("Không tìm thấy thương hiệu với ID: {}", id);
                return null;
            }

            // Kiểm tra trùng tên (ngoại trừ chính nó)
            Optional<ThuongHieu> duplicate = thuongHieuRepository.findByTen(thuongHieu.getTen());
            if (duplicate.isPresent() && !duplicate.get().getThuongHieuId().equals(id)) {
                log.warn("Tên thương hiệu {} đã tồn tại", thuongHieu.getTen());
                return null;
            }

            existing.setTen(thuongHieu.getTen());
            existing.setMoTa(thuongHieu.getMoTa());
            existing.setTrangThai(thuongHieu.getTrangThai());

            ThuongHieu updated = thuongHieuRepository.save(existing);
            log.info("Đã cập nhật thương hiệu: {}", updated.getTen());
            return updated;
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật thương hiệu: ", e);
            return null;
        }
    }

    /**
     * Xóa thương hiệu (soft delete)
     */
    @Transactional
    public boolean xoaThuongHieu(Integer id) {
        try {
            ThuongHieu thuongHieu = thuongHieuRepository.findById(id).orElse(null);
            if (thuongHieu == null) {
                log.warn("Không tìm thấy thương hiệu với ID: {}", id);
                return false;
            }

            thuongHieu.setTrangThai((byte) 0);
            thuongHieuRepository.save(thuongHieu);
            log.info("Đã xóa thương hiệu: {}", thuongHieu.getTen());
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi xóa thương hiệu: ", e);
            return false;
        }
    }

    /**
     * Xóa vĩnh viễn thương hiệu
     */
    @Transactional
    public boolean xoaVinhVienThuongHieu(Integer id) {
        try {
            thuongHieuRepository.deleteById(id);
            log.info("Đã xóa vĩnh viễn thương hiệu ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi xóa vĩnh viễn thương hiệu: ", e);
            return false;
        }
    }

    /**
     * Đếm số lượng thương hiệu
     */
    @Transactional(readOnly = true)
    public long demSoLuongThuongHieu() {
        try {
            return thuongHieuRepository.count();
        } catch (Exception e) {
            log.error("Lỗi khi đếm số lượng thương hiệu: ", e);
            return 0;
        }
    }

    /**
     * Kiểm tra thương hiệu có tồn tại không
     */
    @Transactional(readOnly = true)
    public boolean kiemTraTonTai(Integer id) {
        try {
            return thuongHieuRepository.existsById(id);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra tồn tại thương hiệu: ", e);
            return false;
        }
    }
}
