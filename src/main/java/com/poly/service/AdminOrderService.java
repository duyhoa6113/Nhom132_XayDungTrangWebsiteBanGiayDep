package com.poly.service;

import com.poly.dto.AdminOrderDTO;
import com.poly.dto.OrderItemDTO;
import com.poly.entity.HoaDon;
import com.poly.entity.HoaDonChiTiet;
import com.poly.repository.HoaDonChiTietRepository;
import com.poly.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderService {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;

    /**
     * Lấy tất cả đơn hàng với phân trang
     */
    @Transactional(readOnly = true)
    public Page<AdminOrderDTO> getAllOrders(Pageable pageable) {
        Pageable sortedPageable = org.springframework.data.domain.PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt")
        );
        Page<HoaDon> orders = hoaDonRepository.findAll(sortedPageable);
        // Fetch chi tiết cho mỗi đơn hàng
        orders.getContent().forEach(order -> {
            if (order.getChiTietList() != null) {
                order.getChiTietList().size(); // Force fetch
            }
        });
        return orders.map(this::convertToDTO);
    }

    /**
     * Lấy đơn hàng theo trạng thái
     */
    @Transactional(readOnly = true)
    public Page<AdminOrderDTO> getOrdersByStatus(String trangThai, Pageable pageable) {
        // Tạo Pageable với sort
        Pageable sortedPageable = org.springframework.data.domain.PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt")
        );
        Page<HoaDon> orders = hoaDonRepository.findByTrangThai(trangThai, sortedPageable);
        // Fetch chi tiết cho mỗi đơn hàng
        orders.getContent().forEach(order -> {
            if (order.getChiTietList() != null) {
                order.getChiTietList().size(); // Force fetch
            }
        });
        return orders.map(this::convertToDTO);
    }

    /**
     * Lấy đơn hàng theo ID
     */
    @Transactional(readOnly = true)
    public AdminOrderDTO getOrderById(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
        
        // Force fetch chi tiết
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDonId(id);
        hoaDon.setChiTietList(chiTietList);
        
        return convertToDTO(hoaDon);
    }

    /**
     * Cập nhật trạng thái đơn hàng
     */
    @Transactional
    public AdminOrderDTO updateOrderStatus(Integer id, String trangThaiMoi) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
        
        String trangThaiCu = hoaDon.getTrangThai();
        
        // Validate trạng thái mới
        if (!isValidStatus(trangThaiMoi)) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + trangThaiMoi);
        }
        
        // Cập nhật trạng thái
        hoaDon.setTrangThai(trangThaiMoi);
        HoaDon saved = hoaDonRepository.save(hoaDon);
        
        log.info("Đã cập nhật trạng thái đơn hàng {} từ {} sang {}", 
                hoaDon.getMaHoaDon(), trangThaiCu, trangThaiMoi);
        
        return convertToDTO(saved);
    }

    /**
     * Kiểm tra trạng thái hợp lệ
     */
    private boolean isValidStatus(String status) {
        if (status == null) return false;
        return switch (status) {
            case "ChoXuLy", "DaXacNhan", "DangChuanBi", "DangGiao", 
                 "DaGiao", "HoanThanh", "DaHuy" -> true;
            default -> false;
        };
    }

    /**
     * Lấy thống kê đơn hàng
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getOrderStatistics() {
        Map<String, Long> stats = new HashMap<>();
        
        long totalOrders = hoaDonRepository.countAllOrders();
        stats.put("totalOrders", totalOrders);
        
        // Đếm theo từng trạng thái
        stats.put("choXuLy", hoaDonRepository.countByTrangThai("ChoXuLy"));
        stats.put("daXacNhan", hoaDonRepository.countByTrangThai("DaXacNhan"));
        stats.put("dangGiao", hoaDonRepository.countByTrangThai("DangGiao"));
        stats.put("hoanThanh", hoaDonRepository.countByTrangThai("HoanThanh"));
        stats.put("daHuy", hoaDonRepository.countByTrangThai("DaHuy"));
        
        return stats;
    }

    /**
     * Convert HoaDon sang AdminOrderDTO
     */
    private AdminOrderDTO convertToDTO(HoaDon hoaDon) {
        AdminOrderDTO dto = new AdminOrderDTO();
        dto.setId(hoaDon.getHoaDonId());
        dto.setMaHoaDon(hoaDon.getMaHoaDon());
        
        // Thông tin khách hàng
        if (hoaDon.getKhachHang() != null) {
            dto.setHoTenKhachHang(hoaDon.getKhachHang().getHoTen());
            dto.setEmailKhachHang(hoaDon.getKhachHang().getEmail());
        }
        
        // Thông tin nhận hàng
        dto.setHoTenNhan(hoaDon.getHoTenNhan());
        dto.setSdtNhan(hoaDon.getSdtNhan());
        dto.setDiaChiNhan(hoaDon.getDiaChiNhan());
        dto.setPhuongXa(hoaDon.getPhuongXa());
        dto.setQuanHuyen(hoaDon.getQuanHuyen());
        dto.setTinhTP(hoaDon.getTinhTP());
        
        // Thông tin tài chính
        dto.setTongTien(hoaDon.getTongTien());
        dto.setGiamGia(hoaDon.getGiamGia());
        dto.setPhiVanChuyen(hoaDon.getPhiVanChuyen());
        dto.setTongThanhToan(hoaDon.getTongThanhToan());
        dto.setPhuongThucThanhToan(hoaDon.getPhuongThucThanhToan());
        dto.setTrangThai(hoaDon.getTrangThai());
        dto.setTrangThaiThanhToan(hoaDon.getTrangThaiThanhToan());
        dto.setGhiChu(hoaDon.getGhiChu());
        dto.setCreatedAt(hoaDon.getCreatedAt());
        dto.setUpdatedAt(hoaDon.getUpdatedAt());
        
        // Chi tiết đơn hàng
        List<OrderItemDTO> chiTietList = new ArrayList<>();
        if (hoaDon.getChiTietList() != null && !hoaDon.getChiTietList().isEmpty()) {
            chiTietList = hoaDon.getChiTietList().stream()
                    .map(this::convertItemToDTO)
                    .collect(Collectors.toList());
        }
        dto.setChiTietList(chiTietList);
        
        return dto;
    }

    /**
     * Convert HoaDonChiTiet sang OrderItemDTO
     */
    private OrderItemDTO convertItemToDTO(HoaDonChiTiet item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getHoaDonChiTietId());
        dto.setSoLuong(item.getSoLuong());
        dto.setGia(item.getDonGia());
        dto.setThanhTien(item.getThanhTien());
        
        if (item.getVariant() != null) {
            dto.setSku(item.getVariant().getSKU());
            dto.setHinhAnh(item.getVariant().getHinhAnh());
            
            if (item.getVariant().getSanPham() != null) {
                dto.setTenSanPham(item.getVariant().getSanPham().getTen());
            }
            
            if (item.getVariant().getMauSac() != null) {
                dto.setMauSac(item.getVariant().getMauSac().getTen());
            }
            
            if (item.getVariant().getKichThuoc() != null) {
                dto.setKichThuoc(item.getVariant().getKichThuoc().getTen());
            }
        }
        
        return dto;
    }
}

