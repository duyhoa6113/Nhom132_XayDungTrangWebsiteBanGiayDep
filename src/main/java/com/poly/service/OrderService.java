package com.poly.service;

import com.poly.entity.HoaDon;
import com.poly.entity.HoaDonChiTiet;
import com.poly.entity.KhachHang;
import com.poly.entity.SanPhamChiTiet;
import com.poly.repository.HoaDonRepository;
import com.poly.repository.SanPhamChiTietRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OrderService - Xử lý logic đơn hàng
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final HoaDonRepository hoaDonRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    /**
     * Lấy tất cả đơn hàng của khách hàng
     */
    @Transactional(readOnly = true)
    public Page<HoaDon> getAllOrders(KhachHang khachHang, Pageable pageable) {
        return hoaDonRepository.findByKhachHang(khachHang, pageable);
    }

    /**
     * Lấy đơn hàng theo trạng thái
     */
    @Transactional(readOnly = true)
    public Page<HoaDon> getOrdersByStatus(KhachHang khachHang, String status, Pageable pageable) {
        return hoaDonRepository.findByKhachHangAndTrangThai(khachHang, status, pageable);
    }

    /**
     * Lấy chi tiết đơn hàng
     */
    @Transactional(readOnly = true)
    public HoaDon getOrderDetail(Integer orderId, KhachHang khachHang) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra quyền truy cập
        if (!hoaDon.getKhachHang().getKhachHangId().equals(khachHang.getKhachHangId())) {
            throw new RuntimeException("Bạn không có quyền xem đơn hàng này");
        }

        // Force fetch lazy loading
        if (hoaDon.getChiTietList() != null) {
            hoaDon.getChiTietList().size();
            hoaDon.getChiTietList().forEach(item -> {
                if (item.getVariant() != null) {
                    item.getVariant().getSanPham().getTen();
                    if (item.getVariant().getMauSac() != null) {
                        item.getVariant().getMauSac().getTen();
                    }
                    if (item.getVariant().getKichThuoc() != null) {
                        item.getVariant().getKichThuoc().getTen();
                    }
                }
            });
        }

        return hoaDon;
    }

    /**
     * Hủy đơn hàng
     */
    @Transactional
    public void cancelOrder(Integer orderId, KhachHang khachHang) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra quyền
        if (!hoaDon.getKhachHang().getKhachHangId().equals(khachHang.getKhachHangId())) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }

        // Chỉ cho phép hủy đơn hàng đang chờ xử lý
        if (!"ChoXuLy".equals(hoaDon.getTrangThai())) {
            throw new RuntimeException("Không thể hủy đơn hàng ở trạng thái: " + hoaDon.getTrangThai());
        }

        // Hoàn lại số lượng tồn kho
        hoaDon.getChiTietList().forEach(item -> {
            var variant = item.getVariant();
            variant.setSoLuongTon(variant.getSoLuongTon() + item.getSoLuong());
            sanPhamChiTietRepository.save(variant);
        });

        // Cập nhật trạng thái
        hoaDon.setTrangThai("DaHuy");
        hoaDonRepository.save(hoaDon);

        log.info("Đã hủy đơn hàng: {}", hoaDon.getMaHoaDon());
    }
}