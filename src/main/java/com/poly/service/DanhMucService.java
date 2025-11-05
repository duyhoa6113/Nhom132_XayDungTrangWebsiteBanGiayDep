package com.poly.service;

import com.poly.entity.DanhMuc;
import com.poly.repository.DanhMucRepository;
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
public class DanhMucService {

    private final DanhMucRepository danhMucRepository;

    /**
     * Lấy tất cả danh mục
     */
    @Transactional(readOnly = true)
    public List<DanhMuc> layTatCaDanhMuc() {
        try {
            return danhMucRepository.findAll();
        } catch (Exception e) {
            log.error("Lỗi khi lấy tất cả danh mục: ", e);
            return List.of();
        }
    }

    /**
     * Lấy tất cả danh mục đang hoạt động
     */
    @Transactional(readOnly = true)
    public List<DanhMuc> layDanhMucHoatDong() {
        try {
            return danhMucRepository.findByTrangThai((byte) 1);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh mục hoạt động: ", e);
            return List.of();
        }
    }

    /**
     * Lấy danh mục theo ID
     */
    @Transactional(readOnly = true)
    public DanhMuc layDanhMucTheoId(Integer id) {
        try {
            return danhMucRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh mục theo ID {}: ", id, e);
            return null;
        }
    }

    /**
     * Lấy danh mục theo tên
     */
    @Transactional(readOnly = true)
    public DanhMuc layDanhMucTheoTen(String ten) {
        try {
            return danhMucRepository.findByTen(ten).orElse(null);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh mục theo tên {}: ", ten, e);
            return null;
        }
    }

    /**
     * Lấy danh mục có phân trang
     */
    @Transactional(readOnly = true)
    public Page<DanhMuc> layDanhMucPhanTrang(Pageable pageable) {
        try {
            return danhMucRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh mục phân trang: ", e);
            return Page.empty();
        }
    }

    /**
     * Tìm kiếm danh mục theo tên
     */
    @Transactional(readOnly = true)
    public List<DanhMuc> timKiemTheoTen(String keyword) {
        try {
            return danhMucRepository.findByTenContainingIgnoreCase(keyword);
        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm danh mục: ", e);
            return List.of();
        }
    }

    /**
     * Thêm danh mục mới
     */
    @Transactional
    public DanhMuc themDanhMuc(DanhMuc danhMuc) {
        try {
            // Kiểm tra trùng tên
            if (danhMucRepository.findByTen(danhMuc.getTen()).isPresent()) {
                log.warn("Danh mục với tên {} đã tồn tại", danhMuc.getTen());
                return null;
            }

            danhMuc.setTrangThai((byte) 1);
            danhMuc.setCreatedAt(LocalDateTime.now());

            DanhMuc saved = danhMucRepository.save(danhMuc);
            log.info("Đã thêm danh mục mới: {}", saved.getTen());
            return saved;
        } catch (Exception e) {
            log.error("Lỗi khi thêm danh mục: ", e);
            return null;
        }
    }

    /**
     * Cập nhật danh mục
     */
    @Transactional
    public DanhMuc capNhatDanhMuc(Integer id, DanhMuc danhMuc) {
        try {
            DanhMuc existing = danhMucRepository.findById(id).orElse(null);
            if (existing == null) {
                log.warn("Không tìm thấy danh mục với ID: {}", id);
                return null;
            }

            // Kiểm tra trùng tên (ngoại trừ chính nó)
            Optional<DanhMuc> duplicate = danhMucRepository.findByTen(danhMuc.getTen());
            if (duplicate.isPresent() && !duplicate.get().getDanhMucId().equals(id)) {
                log.warn("Tên danh mục {} đã tồn tại", danhMuc.getTen());
                return null;
            }

            existing.setTen(danhMuc.getTen());
            existing.setMoTa(danhMuc.getMoTa());
            existing.setTrangThai(danhMuc.getTrangThai());

            DanhMuc updated = danhMucRepository.save(existing);
            log.info("Đã cập nhật danh mục: {}", updated.getTen());
            return updated;
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật danh mục: ", e);
            return null;
        }
    }

    /**
     * Xóa danh mục (soft delete)
     */
    @Transactional
    public boolean xoaDanhMuc(Integer id) {
        try {
            DanhMuc danhMuc = danhMucRepository.findById(id).orElse(null);
            if (danhMuc == null) {
                log.warn("Không tìm thấy danh mục với ID: {}", id);
                return false;
            }

            danhMuc.setTrangThai((byte) 0);
            danhMucRepository.save(danhMuc);
            log.info("Đã xóa danh mục: {}", danhMuc.getTen());
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi xóa danh mục: ", e);
            return false;
        }
    }

    /**
     * Xóa vĩnh viễn danh mục
     */
    @Transactional
    public boolean xoaVinhVienDanhMuc(Integer id) {
        try {
            danhMucRepository.deleteById(id);
            log.info("Đã xóa vĩnh viễn danh mục ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi xóa vĩnh viễn danh mục: ", e);
            return false;
        }
    }

    /**
     * Đếm số lượng danh mục
     */
    @Transactional(readOnly = true)
    public long demSoLuongDanhMuc() {
        try {
            return danhMucRepository.count();
        } catch (Exception e) {
            log.error("Lỗi khi đếm số lượng danh mục: ", e);
            return 0;
        }
    }

    /**
     * Kiểm tra danh mục có tồn tại không
     */
    @Transactional(readOnly = true)
    public boolean kiemTraTonTai(Integer id) {
        try {
            return danhMucRepository.existsById(id);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra tồn tại danh mục: ", e);
            return false;
        }
    }

    /**
     * Đếm số sản phẩm theo danh mục
     */
    @Transactional(readOnly = true)
    public long demSoSanPhamTheoDanhMuc(Integer danhMucId) {
        try {
            return danhMucRepository.countSanPhamByDanhMucId(danhMucId);
        } catch (Exception e) {
            log.error("Lỗi khi đếm số sản phẩm theo danh mục: ", e);
            return 0;
        }
    }
}
