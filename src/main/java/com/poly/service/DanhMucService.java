package com.poly.service;

import com.poly.entity.DanhMuc;
import com.poly.dto.DanhMucDTO;
import com.poly.repository.DanhMucRepository;
import com.poly.repository.CategoryWithCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Danh mục
 *
 * @author Nhóm 132
 */
@Service
@Transactional
public class DanhMucService {

    @Autowired
    private DanhMucRepository danhMucRepository;

    /**
     * Lấy tất cả danh mục
     */
    public List<DanhMucDTO> getAllDanhMuc() {
        return danhMucRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh mục đang hoạt động
     */
    public List<DanhMucDTO> getActiveDanhMuc() {
        return danhMucRepository.findByTrangThai(1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh mục đang hoạt động và sắp xếp theo tên
     */
    public List<DanhMucDTO> getActiveDanhMucSorted() {
        return danhMucRepository.findByTrangThaiOrderByTenAsc(1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh mục theo ID
     */
    public Optional<DanhMucDTO> getDanhMucById(Integer id) {
        return danhMucRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Lấy danh mục theo ID và trạng thái active
     */
    public DanhMuc getActiveDanhMucById(Integer id) {
        return danhMucRepository.findByIdAndActive(id);
    }

    /**
     * Lấy tất cả danh mục kèm số lượng sản phẩm
     */
    public List<CategoryWithCount> getAllCategoriesWithProductCount() {
        return danhMucRepository.findAllCategoriesWithProductCount();
    }

    /**
     * Lấy danh mục có sản phẩm (không lấy danh mục rỗng)
     */
    public List<DanhMucDTO> getCategoriesWithProducts() {
        return danhMucRepository.findCategoriesWithProducts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm danh mục theo từ khóa
     */
    public List<DanhMucDTO> searchCategories(String keyword) {
        return danhMucRepository.searchCategories(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Đếm số sản phẩm trong danh mục
     */
    public long countProductsInCategory(Integer danhMucId) {
        return danhMucRepository.countProductsInCategory(danhMucId);
    }

    /**
     * Lấy top danh mục theo số lượng sản phẩm
     */
    public List<DanhMucDTO> getTopCategoriesByProductCount(int limit) {
        return danhMucRepository.findTopCategoriesByProductCount(limit).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Thêm mới danh mục
     */
    public DanhMucDTO createDanhMuc(DanhMucDTO dto) {
        // Validate
        validateDanhMuc(dto);

        // Kiểm tra tên đã tồn tại
        if (danhMucRepository.existsByTen(dto.getTen())) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }

        // Convert DTO to Entity
        DanhMuc danhMuc = convertToEntity(dto);

        // Save to database
        DanhMuc savedDanhMuc = danhMucRepository.save(danhMuc);

        return convertToDTO(savedDanhMuc);
    }

    /**
     * Cập nhật danh mục
     */
    public DanhMucDTO updateDanhMuc(Integer id, DanhMucDTO dto) {
        // Validate
        validateDanhMuc(dto);

        // Kiểm tra danh mục có tồn tại không
        DanhMuc existingDanhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + id));

        // Kiểm tra tên đã tồn tại (trừ chính nó)
        if (danhMucRepository.existsByTenAndNotId(dto.getTen(), id)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }

        // Update entity
        existingDanhMuc.setTen(dto.getTen());
        existingDanhMuc.setMoTa(dto.getMoTa());
        existingDanhMuc.setTrangThai(dto.getTrangThai());

        // Save
        DanhMuc updatedDanhMuc = danhMucRepository.save(existingDanhMuc);
        return convertToDTO(updatedDanhMuc);
    }

    /**
     * Xóa mềm danh mục (chuyển trạng thái = 0)
     */
    public boolean softDeleteDanhMuc(Integer id) {
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + id));

        danhMuc.setTrangThai(0);
        danhMucRepository.save(danhMuc);
        return true;
    }

    /**
     * Xóa vĩnh viễn danh mục
     */
    public boolean deleteDanhMuc(Integer id) {
        // Kiểm tra danh mục có tồn tại không
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + id));

        // Kiểm tra có sản phẩm nào đang dùng danh mục này không
        long productCount = danhMucRepository.countProductsInCategory(id);
        if (productCount > 0) {
            throw new IllegalArgumentException("Không thể xóa danh mục đang được sử dụng bởi " + productCount + " sản phẩm");
        }

        danhMucRepository.delete(danhMuc);
        return true;
    }

    /**
     * Thay đổi trạng thái danh mục
     */
    public boolean changeStatus(Integer id, Integer trangThai) {
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + id));

        danhMuc.setTrangThai(trangThai);
        danhMucRepository.save(danhMuc);
        return true;
    }

    /**
     * Đếm tổng số danh mục
     */
    public long getTotalCount() {
        return danhMucRepository.count();
    }

    /**
     * Đếm số danh mục đang hoạt động
     */
    public long getActiveCount() {
        return danhMucRepository.countByTrangThai(1);
    }

    /**
     * Đếm số danh mục tạm ngừng
     */
    public long getInactiveCount() {
        return danhMucRepository.countByTrangThai(0);
    }

    /**
     * Validate dữ liệu danh mục
     */
    private void validateDanhMuc(DanhMucDTO dto) {
        if (dto.getTen() == null || dto.getTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }

        if (dto.getTen().length() > 150) {
            throw new IllegalArgumentException("Tên danh mục không được vượt quá 150 ký tự");
        }

        if (dto.getMoTa() != null && dto.getMoTa().length() > 1000) {
            throw new IllegalArgumentException("Mô tả không được vượt quá 1000 ký tự");
        }

        if (dto.getTrangThai() != null && (dto.getTrangThai() < 0 || dto.getTrangThai() > 1)) {
            throw new IllegalArgumentException("Trạng thái chỉ nhận giá trị 0 hoặc 1");
        }
    }

    /**
     * Convert Entity to DTO
     */
    private DanhMucDTO convertToDTO(DanhMuc danhMuc) {
        DanhMucDTO dto = new DanhMucDTO();
        dto.setDanhMucId(danhMuc.getDanhMucId());
        dto.setTen(danhMuc.getTen());
        dto.setMoTa(danhMuc.getMoTa());
        dto.setTrangThai(danhMuc.getTrangThai());
        return dto;
    }

    /**
     * Convert DTO to Entity
     */
    private DanhMuc convertToEntity(DanhMucDTO dto) {
        DanhMuc danhMuc = new DanhMuc();
        if (dto.getDanhMucId() != null) {
            danhMuc.setDanhMucId(dto.getDanhMucId());
        }
        danhMuc.setTen(dto.getTen());
        danhMuc.setMoTa(dto.getMoTa());
        danhMuc.setTrangThai(dto.getTrangThai());
        return danhMuc;
    }
}