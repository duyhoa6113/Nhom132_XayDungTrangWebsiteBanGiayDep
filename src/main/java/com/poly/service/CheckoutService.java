package com.poly.service;

import com.poly.dto.CheckoutData;
import com.poly.dto.CheckoutRequest;
import com.poly.entity.*;
import com.poly.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final GioHangRepository gioHangRepository;
    private final DiaChiRepository diaChiRepository;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    /**
     * TRẢ VỀ CHECKOUT DATA ĐÚNG NHƯ CONTROLLER MONG MUỐN
     */
    @Transactional(readOnly = true)
    public CheckoutData prepareCheckout(KhachHang khachHang, List<Integer> cartItemIds) {

        List<GioHang> cartItems;

        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            cartItems = gioHangRepository.findByKhachHangAndGioHangIdIn(khachHang, cartItemIds);
        } else {
            cartItems = gioHangRepository.findByKhachHangOrderByCreatedAtDesc(khachHang);
        }

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        List<Map<String, Object>> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (GioHang cart : cartItems) {

            SanPhamChiTiet variant = cart.getVariant();

            BigDecimal itemTotal = variant.getGiaBan()
                    .multiply(BigDecimal.valueOf(cart.getSoLuong()));

            subtotal = subtotal.add(itemTotal);

            Map<String, Object> item = new HashMap<>();
            item.put("gioHangId", cart.getGioHangId());
            item.put("variantId", variant.getVariantId());
            item.put("tenSanPham", variant.getSanPham().getTen());
            item.put("hinhAnh",
                    variant.getHinhAnh() != null ? variant.getHinhAnh() : "/img/no-image.png");
            item.put("mauSac", variant.getMauSac() != null ? variant.getMauSac().getTen() : "");
            item.put("kichThuoc", variant.getKichThuoc() != null ? variant.getKichThuoc().getTen() : "");
            item.put("giaBan", variant.getGiaBan());
            item.put("soLuong", cart.getSoLuong());
            item.put("tongTien", itemTotal);

            items.add(item);
        }

        BigDecimal shippingFee = new BigDecimal("30000");
        BigDecimal discount = BigDecimal.ZERO; // mặc định không khuyến mãi
        BigDecimal finalAmount = subtotal.add(shippingFee);

        // Tạo DTO trả về cho controller
        CheckoutData result = new CheckoutData();
        result.setItems(items);
        result.setSubtotal(subtotal);
        result.setShippingFee(shippingFee);
        result.setDiscount(discount);
        result.setFinalAmount(finalAmount);

        return result;
    }

    /**
     * TẠO ĐƠN HÀNG — ĐÚNG KIỂU CONTROLLER GỌI
     */
    @Transactional
    public HoaDon createOrder(KhachHang khachHang, CheckoutRequest request) {

        if (request.getAddressId() == null) {
            throw new RuntimeException("Vui lòng chọn địa chỉ giao hàng");
        }

        DiaChi address = diaChiRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        // Lấy giỏ hàng
        List<GioHang> cartItems;
        if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
            cartItems = gioHangRepository.findByKhachHangAndGioHangIdIn(khachHang, request.getCartItemIds());
        } else {
            cartItems = gioHangRepository.findByKhachHangOrderByCreatedAtDesc(khachHang);
        }

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // Tính tổng tiền
        BigDecimal tongTien = BigDecimal.ZERO;

        for (GioHang cart : cartItems) {
            SanPhamChiTiet variant = cart.getVariant();

            if (variant.getSoLuongTon() < cart.getSoLuong()) {
                throw new RuntimeException("Sản phẩm " + variant.getSanPham().getTen() + " không đủ hàng");
            }

            tongTien = tongTien.add(
                    variant.getGiaBan().multiply(BigDecimal.valueOf(cart.getSoLuong()))
            );
        }

        BigDecimal phiShip = new BigDecimal("30000");
        BigDecimal tongThanhToan = tongTien.add(phiShip);

        // Tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon("HD" + System.currentTimeMillis());
        hoaDon.setKhachHang(khachHang);
        hoaDon.setHoTenNhan(address.getHoTenNhan());
        hoaDon.setSdtNhan(address.getSdtNhan());
        hoaDon.setDiaChiNhan(address.getDiaChi());
        hoaDon.setPhuongXa(address.getPhuongXa());
        hoaDon.setQuanHuyen(address.getQuanHuyen());
        hoaDon.setTinhTP(address.getTinhTP());
        hoaDon.setPhuongThucThanhToan(request.getPaymentMethod());
        hoaDon.setTrangThai("ChoXuLy");
        hoaDon.setTongTien(tongTien);
        hoaDon.setPhiVanChuyen(phiShip);
        hoaDon.setGiamGia(BigDecimal.ZERO);
        hoaDon.setTongThanhToan(tongThanhToan);

        hoaDon = hoaDonRepository.save(hoaDon);

        // Tạo chi tiết đơn hàng & cập nhật kho
        for (GioHang cart : cartItems) {

            SanPhamChiTiet variant = cart.getVariant();

            HoaDonChiTiet ct = new HoaDonChiTiet();
            ct.setHoaDon(hoaDon);
            ct.setVariant(variant);
            ct.setSoLuong(cart.getSoLuong());
            ct.setDonGia(variant.getGiaBan());
            ct.setThanhTien(
                    variant.getGiaBan().multiply(BigDecimal.valueOf(cart.getSoLuong()))
            );
            hoaDonChiTietRepository.save(ct);

            variant.setSoLuongTon(variant.getSoLuongTon() - cart.getSoLuong());
            sanPhamChiTietRepository.save(variant);
        }

        // Xóa giỏ hàng
        if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
            gioHangRepository.deleteByKhachHangAndGioHangIdIn(khachHang, request.getCartItemIds());
        } else {
            gioHangRepository.deleteByKhachHang(khachHang);
        }

        return hoaDon;
    }

    /**
     * LẤY ĐƠN HÀNG CHO TRANG SUCCESS — ĐÃ FIX LAZY LOADING
     */
    @Transactional(readOnly = true)
    public HoaDon getOrderById(Integer orderId, KhachHang khachHang) {

        HoaDon hoaDon = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!hoaDon.getKhachHang().getKhachHangId().equals(khachHang.getKhachHangId())) {
            throw new RuntimeException("Bạn không có quyền xem đơn này");
        }

        // ✅ FIX: FORCE FETCH LAZY LOADING
        if (hoaDon.getChiTietList() != null) {
            hoaDon.getChiTietList().size(); // Trigger lazy loading

            // Fetch nested relationships
            hoaDon.getChiTietList().forEach(item -> {
                if (item.getVariant() != null) {
                    // Force load variant details
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

}