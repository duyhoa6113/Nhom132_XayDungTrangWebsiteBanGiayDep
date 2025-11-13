package com.poly.service;

import com.poly.dto.CheckoutRequest;
import com.poly.dto.OrderItemDTO;
import com.poly.dto.OrderResponse;
import com.poly.entity.*;
import com.poly.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final GioHangRepository gioHangRepository;
    private final DiaChiRepository diaChiRepository;
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final SanPhamChiTietRepository variantRepository;

    private static final BigDecimal PHI_VAN_CHUYEN_MAC_DINH = new BigDecimal("30000");

    /**
     * Tạo đơn hàng từ giỏ hàng
     */
    @Transactional
    public OrderResponse createOrder(KhachHang khachHang, CheckoutRequest request) {
        // Lấy cart items
        List<GioHang> gioHangList = gioHangRepository
                .findByKhachHangAndGioHangIdIn(khachHang, request.getCartItemIds());

        if (gioHangList.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // Kiểm tra tồn kho cho tất cả sản phẩm
        for (GioHang gioHang : gioHangList) {
            SanPhamChiTiet variant = gioHang.getVariant();
            if (variant.getSoLuongTon() < gioHang.getSoLuong()) {
                throw new RuntimeException("Sản phẩm " + variant.getSanPham().getTen()
                        + " không đủ số lượng trong kho");
            }
        }

        // Tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(generateOrderCode());
        hoaDon.setKhachHang(khachHang);

        // Xử lý địa chỉ
        if (request.getDiaChiId() != null) {
            DiaChi diaChi = diaChiRepository.findById(request.getDiaChiId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

            hoaDon.setHoTenNhan(diaChi.getHoTenNhan());
            hoaDon.setSdtNhan(diaChi.getSdtNhan());
            hoaDon.setDiaChiNhan(diaChi.getDiaChi());
            hoaDon.setPhuongXa(diaChi.getPhuongXa());
            hoaDon.setQuanHuyen(diaChi.getQuanHuyen());
            hoaDon.setTinhTP(diaChi.getTinhTP());
        } else {
            // Tạo địa chỉ mới
            hoaDon.setHoTenNhan(request.getHoTenNhan());
            hoaDon.setSdtNhan(request.getSdtNhan());
            hoaDon.setDiaChiNhan(request.getDiaChi());
            hoaDon.setPhuongXa(request.getPhuongXa());
            hoaDon.setQuanHuyen(request.getQuanHuyen());
            hoaDon.setTinhTP(request.getTinhTP());
        }

        // Tính tổng tiền hàng
        BigDecimal tongTien = BigDecimal.ZERO;
        for (GioHang gioHang : gioHangList) {
            tongTien = tongTien.add(gioHang.getTongTien());
        }
        hoaDon.setTongTien(tongTien);

        // Phí vận chuyển
        hoaDon.setPhiVanChuyen(PHI_VAN_CHUYEN_MAC_DINH);

        // Xử lý khuyến mãi
        BigDecimal giamGia = BigDecimal.ZERO;
        if (request.getMaKhuyenMai() != null && !request.getMaKhuyenMai().isEmpty()) {
            KhuyenMai khuyenMai = khuyenMaiRepository
                    .findByMa(request.getMaKhuyenMai())
                    .orElse(null);

            if (khuyenMai != null && khuyenMai.getTrangThai() == 1) {
                // Kiểm tra điều kiện áp dụng
                if (tongTien.compareTo(khuyenMai.getDieuKienApDung()) >= 0) {
                    giamGia = calculateDiscount(khuyenMai, tongTien);
                    hoaDon.setKhuyenMai(khuyenMai);

                    // Giảm số lượng voucher
                    if (khuyenMai.getSoLuong() > 0) {
                        khuyenMai.setSoLuong(khuyenMai.getSoLuong() - 1);
                        khuyenMaiRepository.save(khuyenMai);
                    }
                }
            }
        }
        hoaDon.setGiamGia(giamGia);

        // Tổng thanh toán
        BigDecimal tongThanhToan = tongTien
                .add(hoaDon.getPhiVanChuyen())
                .subtract(giamGia);
        hoaDon.setTongThanhToan(tongThanhToan);

        // Phương thức thanh toán
        hoaDon.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        hoaDon.setTrangThai("ChoXuLy");
        hoaDon.setGhiChu(request.getGhiChu());

        // Lưu hóa đơn
        hoaDon = hoaDonRepository.save(hoaDon);

        // Tạo chi tiết hóa đơn
        for (GioHang gioHang : gioHangList) {
            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setVariant(gioHang.getVariant());
            chiTiet.setSoLuong(gioHang.getSoLuong());
            chiTiet.setDonGia(gioHang.getVariant().getGiaBan());

            hoaDonChiTietRepository.save(chiTiet);

            // Trừ tồn kho
            SanPhamChiTiet variant = gioHang.getVariant();
            variant.setSoLuongTon(variant.getSoLuongTon() - gioHang.getSoLuong());
            variantRepository.save(variant);
        }

        // Xóa cart items đã đặt hàng
        gioHangRepository.deleteAll(gioHangList);

        log.info("Created order: {}", hoaDon.getMaHoaDon());

        return convertToOrderResponse(hoaDon);
    }

    /**
     * Lấy danh sách đơn hàng
     */
    public List<OrderResponse> getOrders(KhachHang khachHang) {
        List<HoaDon> orders = hoaDonRepository
                .findByKhachHangOrderByCreatedAtDesc(khachHang);

        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy đơn hàng theo trạng thái
     */
    public List<OrderResponse> getOrdersByStatus(KhachHang khachHang, String status) {
        List<HoaDon> orders = hoaDonRepository
                .findByKhachHangAndTrangThaiOrderByCreatedAtDesc(khachHang, status);

        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết đơn hàng
     */
    public OrderResponse getOrderDetail(KhachHang khachHang, Integer hoaDonId) {
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra quyền sở hữu
        if (!hoaDon.getKhachHang().getKhachHangId().equals(khachHang.getKhachHangId())) {
            throw new RuntimeException("Không có quyền truy cập");
        }

        return convertToOrderResponse(hoaDon);
    }

    /**
     * Lấy đơn hàng theo mã
     */
    public OrderResponse getOrderByCode(KhachHang khachHang, String maHoaDon) {
        HoaDon hoaDon = hoaDonRepository.findByMaHoaDon(maHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra quyền sở hữu
        if (!hoaDon.getKhachHang().getKhachHangId().equals(khachHang.getKhachHangId())) {
            throw new RuntimeException("Không có quyền truy cập");
        }

        return convertToOrderResponse(hoaDon);
    }

    /**
     * Hủy đơn hàng
     */
    @Transactional
    public void cancelOrder(KhachHang khachHang, Integer hoaDonId, String lyDo) {
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra quyền sở hữu
        if (!hoaDon.getKhachHang().getKhachHangId().equals(khachHang.getKhachHangId())) {
            throw new RuntimeException("Không có quyền thao tác");
        }

        // Chỉ cho phép hủy nếu đơn hàng đang chờ xử lý
        if (!"ChoXuLy".equals(hoaDon.getTrangThai())) {
            throw new RuntimeException("Không thể hủy đơn hàng này");
        }

        hoaDon.setTrangThai("DaHuy");
        hoaDon.setGhiChu(hoaDon.getGhiChu() + "\nLý do hủy: " + lyDo);

        // Hoàn lại tồn kho
        for (HoaDonChiTiet chiTiet : hoaDon.getChiTietList()) {
            SanPhamChiTiet variant = chiTiet.getVariant();
            variant.setSoLuongTon(variant.getSoLuongTon() + chiTiet.getSoLuong());
            variantRepository.save(variant);
        }

        hoaDonRepository.save(hoaDon);
        log.info("Cancelled order: {}", hoaDon.getMaHoaDon());
    }

    /**
     * Đếm số đơn hàng theo trạng thái
     */
    public Long countOrdersByStatus(KhachHang khachHang, String status) {
        return hoaDonRepository.countByKhachHangAndTrangThai(khachHang, status);
    }

    /**
     * Generate mã đơn hàng
     */
    private String generateOrderCode() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "HD" + timestamp;
    }

    /**
     * Tính giảm giá
     */
    private BigDecimal calculateDiscount(KhuyenMai khuyenMai, BigDecimal tongTien) {
        BigDecimal giamGia;

        if ("percent".equals(khuyenMai.getLoai())) {
            // Giảm theo phần trăm
            giamGia = tongTien.multiply(khuyenMai.getGiaTri())
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

            // Kiểm tra giảm tối đa
            if (khuyenMai.getGiamToiDa() != null &&
                    giamGia.compareTo(khuyenMai.getGiamToiDa()) > 0) {
                giamGia = khuyenMai.getGiamToiDa();
            }
        } else {
            // Giảm cố định
            giamGia = khuyenMai.getGiaTri();
        }

        return giamGia;
    }

    /**
     * Convert to OrderResponse
     */
    private OrderResponse convertToOrderResponse(HoaDon hoaDon) {
        OrderResponse response = new OrderResponse();
        response.setId(hoaDon.getHoaDonId());
        response.setMaDonHang(hoaDon.getMaHoaDon());
        response.setHoTenNhan(hoaDon.getHoTenNhan());
        response.setSdtNhan(hoaDon.getSdtNhan());

        // Ghép địa chỉ đầy đủ
        StringBuilder diaChiFull = new StringBuilder(hoaDon.getDiaChiNhan());
        if (hoaDon.getPhuongXa() != null && !hoaDon.getPhuongXa().isEmpty()) {
            diaChiFull.append(", ").append(hoaDon.getPhuongXa());
        }
        if (hoaDon.getQuanHuyen() != null && !hoaDon.getQuanHuyen().isEmpty()) {
            diaChiFull.append(", ").append(hoaDon.getQuanHuyen());
        }
        if (hoaDon.getTinhTP() != null && !hoaDon.getTinhTP().isEmpty()) {
            diaChiFull.append(", ").append(hoaDon.getTinhTP());
        }
        response.setDiaChiGiaoHang(diaChiFull.toString());

        response.setTongTienHang(hoaDon.getTongTien());
        response.setPhiVanChuyen(hoaDon.getPhiVanChuyen());
        response.setGiamGiaKhuyenMai(hoaDon.getGiamGia());
        response.setTongThanhToan(hoaDon.getTongThanhToan());
        response.setPhuongThucThanhToan(hoaDon.getPhuongThucThanhToan());
        response.setTrangThaiDonHang(hoaDon.getTrangThai());
        response.setTrangThaiThanhToan(getTrangThaiThanhToan(hoaDon));
        response.setGhiChu(hoaDon.getGhiChu());
        response.setNgayDat(hoaDon.getCreatedAt());

        // Chi tiết đơn hàng
        List<OrderItemDTO> items = hoaDon.getChiTietList().stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList());
        response.setChiTietList(items);

        return response;
    }

    private String getTrangThaiThanhToan(HoaDon hoaDon) {
        if ("COD".equalsIgnoreCase(hoaDon.getPhuongThucThanhToan())) {
            return "HoanThanh".equals(hoaDon.getTrangThai())
                    ? "DA_THANH_TOAN" : "CHUA_THANH_TOAN";
        }
        return "DA_THANH_TOAN";
    }

    private OrderItemDTO convertToOrderItemDTO(HoaDonChiTiet chiTiet) {
        SanPhamChiTiet variant = chiTiet.getVariant();

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(chiTiet.getHoaDonChiTietId());
        dto.setTenSanPham(variant.getSanPham().getTen());
        dto.setHinhAnh(variant.getHinhAnh());
        dto.setMauSac(variant.getMauSac().getTen());
        dto.setKichThuoc(variant.getKichThuoc().getTen());
        dto.setSoLuong(chiTiet.getSoLuong());
        dto.setGia(chiTiet.getDonGia());
        dto.setThanhTien(chiTiet.getThanhTien());
        dto.setSku(variant.getSKU());

        return dto;
    }
}