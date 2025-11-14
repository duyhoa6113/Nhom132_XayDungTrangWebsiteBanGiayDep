package com.poly.service;

import com.poly.dto.DiaChiDTO;
import com.poly.entity.DiaChi;
import com.poly.entity.KhachHang;
import com.poly.repository.DiaChiRepository;
import com.poly.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiaChiService {

    @Autowired
    private DiaChiRepository diaChiRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    /**
     * Lấy tất cả địa chỉ của khách hàng theo ID
     */
    public List<DiaChi> getAddressesByKhachHangId(Integer khachHangId) {
        return diaChiRepository.findByKhachHangId(khachHangId);
    }

    /**
     * Lấy tất cả địa chỉ của khách hàng (alias method)
     */
    public List<DiaChi> getAddressesByCustomer(Integer khachHangId) {
        return getAddressesByKhachHangId(khachHangId);
    }

    /**
     * Lấy địa chỉ theo ID
     */
    public DiaChi getAddressById(Integer diaChiId) {
        return diaChiRepository.findById(diaChiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
    }

    /**
     * Lấy địa chỉ mặc định của khách hàng
     */
    public Optional<DiaChi> getDefaultAddress(Integer khachHangId) {
        return diaChiRepository.findDefaultAddressByKhachHangId(khachHangId);
    }

    /**
     * Kiểm tra địa chỉ có thuộc về khách hàng không
     */
    public boolean isAddressBelongsToCustomer(Integer diaChiId, Integer khachHangId) {
        return diaChiRepository.existsByIdAndKhachHangId(diaChiId, khachHangId);
    }

    /**
     * Thêm địa chỉ mới
     */
    @Transactional
    public DiaChi addAddress(Integer khachHangId, DiaChiDTO diaChiDTO) {
        // Kiểm tra khách hàng tồn tại
        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Nếu đặt làm mặc định, bỏ mặc định của các địa chỉ khác
        if (Boolean.TRUE.equals(diaChiDTO.getMacDinh())) {
            removeDefaultAddress(khachHangId);
        }

        // Nếu đây là địa chỉ đầu tiên, tự động đặt làm mặc định
        long addressCount = diaChiRepository.countByKhachHangId(khachHangId);
        if (addressCount == 0) {
            diaChiDTO.setMacDinh(true);
        }

        // Tạo entity mới
        DiaChi diaChi = new DiaChi();
        diaChi.setKhachHang(khachHang);
        diaChi.setHoTenNhan(diaChiDTO.getHoTenNhan());
        diaChi.setSdtNhan(diaChiDTO.getSdtNhan());
        diaChi.setTinhTP(diaChiDTO.getTinhTP());
        diaChi.setQuanHuyen(diaChiDTO.getQuanHuyen());
        diaChi.setPhuongXa(diaChiDTO.getPhuongXa());
        diaChi.setDiaChi(diaChiDTO.getDiaChi());
        diaChi.setMacDinh(diaChiDTO.getMacDinh() != null ? diaChiDTO.getMacDinh() : false);
        diaChi.setCreatedAt(LocalDateTime.now());

        return diaChiRepository.save(diaChi);
    }

    /**
     * Cập nhật địa chỉ
     */
    @Transactional
    public DiaChi updateAddress(Integer khachHangId, DiaChiDTO diaChiDTO) {
        // Kiểm tra địa chỉ tồn tại và thuộc về khách hàng
        DiaChi diaChi = diaChiRepository.findById(diaChiDTO.getDiaChiId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        if (!diaChi.getKhachHang().getKhachHangId().equals(khachHangId)) {
            throw new RuntimeException("Bạn không có quyền sửa địa chỉ này");
        }

        // Nếu đặt làm mặc định, bỏ mặc định của các địa chỉ khác
        if (Boolean.TRUE.equals(diaChiDTO.getMacDinh())) {
            removeDefaultAddress(khachHangId);
        }

        // Cập nhật thông tin
        diaChi.setHoTenNhan(diaChiDTO.getHoTenNhan());
        diaChi.setSdtNhan(diaChiDTO.getSdtNhan());
        diaChi.setTinhTP(diaChiDTO.getTinhTP());
        diaChi.setQuanHuyen(diaChiDTO.getQuanHuyen());
        diaChi.setPhuongXa(diaChiDTO.getPhuongXa());
        diaChi.setDiaChi(diaChiDTO.getDiaChi());
        diaChi.setMacDinh(diaChiDTO.getMacDinh() != null ? diaChiDTO.getMacDinh() : false);
        diaChi.setUpdatedAt(LocalDateTime.now());

        return diaChiRepository.save(diaChi);
    }

    /**
     * Xóa địa chỉ
     */
    @Transactional
    public void deleteAddress(Integer khachHangId, Integer diaChiId) {
        DiaChi diaChi = diaChiRepository.findById(diaChiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        if (!diaChi.getKhachHang().getKhachHangId().equals(khachHangId)) {
            throw new RuntimeException("Bạn không có quyền xóa địa chỉ này");
        }

        boolean wasMacDinh = Boolean.TRUE.equals(diaChi.getMacDinh());

        diaChiRepository.delete(diaChi);

        // Nếu xóa địa chỉ mặc định, đặt địa chỉ đầu tiên còn lại làm mặc định
        if (wasMacDinh) {
            List<DiaChi> remainingAddresses = diaChiRepository.findByKhachHangId(khachHangId);
            if (!remainingAddresses.isEmpty()) {
                DiaChi firstAddress = remainingAddresses.get(0);
                firstAddress.setMacDinh(true);
                firstAddress.setUpdatedAt(LocalDateTime.now());
                diaChiRepository.save(firstAddress);
            }
        }
    }

    /**
     * Bỏ địa chỉ mặc định của tất cả địa chỉ khác
     */
    @Transactional
    public void removeDefaultAddress(Integer khachHangId) {
        List<DiaChi> defaultAddresses = diaChiRepository.findAllDefaultAddressesByKhachHangId(khachHangId);
        for (DiaChi addr : defaultAddresses) {
            addr.setMacDinh(false);
            addr.setUpdatedAt(LocalDateTime.now());
            diaChiRepository.save(addr);
        }
    }

    /**
     * Đặt địa chỉ làm mặc định
     */
    @Transactional
    public void setDefaultAddress(Integer khachHangId, Integer diaChiId) {
        DiaChi diaChi = diaChiRepository.findById(diaChiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        if (!diaChi.getKhachHang().getKhachHangId().equals(khachHangId)) {
            throw new RuntimeException("Bạn không có quyền thay đổi địa chỉ này");
        }

        // Bỏ mặc định của các địa chỉ khác
        removeDefaultAddress(khachHangId);

        // Đặt địa chỉ này làm mặc định
        diaChi.setMacDinh(true);
        diaChi.setUpdatedAt(LocalDateTime.now());
        diaChiRepository.save(diaChi);
    }

    /**
     * Đếm số lượng địa chỉ của khách hàng
     */
    public long countAddresses(Integer khachHangId) {
        return diaChiRepository.countByKhachHangId(khachHangId);
    }

    /**
     * Kiểm tra khách hàng có địa chỉ nào chưa
     */
    public boolean hasAddress(Integer khachHangId) {
        return diaChiRepository.countByKhachHangId(khachHangId) > 0;
    }
}