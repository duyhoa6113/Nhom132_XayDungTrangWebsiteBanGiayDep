package com.poly.service;

import com.poly.dto.VaiTroDTO;
import com.poly.entity.VaiTro;
import com.poly.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VaiTroService {

    private final VaiTroRepository vaiTroRepository;

    /**
     * Lấy tất cả vai trò
     */
    public List<VaiTroDTO> getAllVaiTro() {
        return vaiTroRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy vai trò theo ID
     */
    public VaiTroDTO getVaiTroById(Integer id) {
        VaiTro vaiTro = vaiTroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với ID: " + id));
        return convertToDTO(vaiTro);
    }

    /**
     * Tạo vai trò mới
     */
    @Transactional
    public VaiTroDTO createVaiTro(VaiTroDTO vaiTroDTO) {
        // Kiểm tra ID đã tồn tại chưa
        if (vaiTroRepository.existsById(vaiTroDTO.getVaiTroId())) {
            throw new RuntimeException("ID vai trò đã tồn tại: " + vaiTroDTO.getVaiTroId());
        }

        // Kiểm tra tên vai trò đã tồn tại chưa
        if (vaiTroRepository.existsByTenVaiTro(vaiTroDTO.getTenVaiTro())) {
            throw new RuntimeException("Tên vai trò đã tồn tại: " + vaiTroDTO.getTenVaiTro());
        }

        VaiTro vaiTro = convertToEntity(vaiTroDTO);
        vaiTro.setCreatedAt(LocalDateTime.now());

        VaiTro savedVaiTro = vaiTroRepository.save(vaiTro);
        return convertToDTO(savedVaiTro);
    }

    /**
     * Cập nhật vai trò
     */
    @Transactional
    public VaiTroDTO updateVaiTro(Integer id, VaiTroDTO vaiTroDTO) {
        VaiTro existingVaiTro = vaiTroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với ID: " + id));

        // Kiểm tra tên vai trò đã tồn tại chưa (trừ vai trò hiện tại)
        if (vaiTroRepository.existsByTenVaiTroAndNotId(vaiTroDTO.getTenVaiTro(), id)) {
            throw new RuntimeException("Tên vai trò đã tồn tại: " + vaiTroDTO.getTenVaiTro());
        }

        existingVaiTro.setTenVaiTro(vaiTroDTO.getTenVaiTro());
        existingVaiTro.setMoTa(vaiTroDTO.getMoTa());

        VaiTro updatedVaiTro = vaiTroRepository.save(existingVaiTro);
        return convertToDTO(updatedVaiTro);
    }

    /**
     * Xóa vai trò
     */
    @Transactional
    public void deleteVaiTro(Integer id) {
        VaiTro vaiTro = vaiTroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với ID: " + id));

        // Kiểm tra xem có nhân viên nào đang sử dụng vai trò này không
        Long countNhanVien = vaiTroRepository.countNhanVienByVaiTroId(id);
        if (countNhanVien > 0) {
            throw new RuntimeException("Không thể xóa vai trò này vì có " + countNhanVien + " nhân viên đang sử dụng");
        }

        vaiTroRepository.delete(vaiTro);
    }

    /**
     * Kiểm tra vai trò có tồn tại không
     */
    public boolean existsById(Integer id) {
        return vaiTroRepository.existsById(id);
    }

    /**
     * Đếm số lượng nhân viên theo vai trò
     */
    public Long countNhanVienByVaiTroId(Integer vaiTroId) {
        return vaiTroRepository.countNhanVienByVaiTroId(vaiTroId);
    }

    /**
     * Convert Entity sang DTO
     */
    private VaiTroDTO convertToDTO(VaiTro vaiTro) {
        VaiTroDTO dto = new VaiTroDTO();
        dto.setVaiTroId(vaiTro.getVaiTroId());
        dto.setTenVaiTro(vaiTro.getTenVaiTro());
        dto.setMoTa(vaiTro.getMoTa());
        dto.setCreatedAt(vaiTro.getCreatedAt());

        // Đếm số lượng nhân viên
        Long count = vaiTroRepository.countNhanVienByVaiTroId(vaiTro.getVaiTroId());
        dto.setSoLuongNhanVien(count != null ? count.intValue() : 0);

        return dto;
    }

    /**
     * Convert DTO sang Entity
     */
    private VaiTro convertToEntity(VaiTroDTO dto) {
        VaiTro vaiTro = new VaiTro();
        vaiTro.setVaiTroId(dto.getVaiTroId());
        vaiTro.setTenVaiTro(dto.getTenVaiTro());
        vaiTro.setMoTa(dto.getMoTa());
        return vaiTro;
    }
}