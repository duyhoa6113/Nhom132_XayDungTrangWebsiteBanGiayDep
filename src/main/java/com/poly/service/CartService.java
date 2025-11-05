package com.poly.service;

import com.poly.dto.AddToCartRequest;
import com.poly.dto.CartItemDTO;
import com.poly.dto.CartSummaryDTO;
import com.poly.dto.UpdateCartItemRequest;
import com.poly.entity.*;
import com.poly.repository.GioHangChiTietRepository;
import com.poly.repository.GioHangRepository;
import com.poly.repository.KhachHangRepository;
import com.poly.repository.SanPhamChiTietRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ giỏ hàng
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final GioHangRepository gioHangRepository;
    private final GioHangChiTietRepository gioHangChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final KhachHangRepository khachHangRepository;

    /**
     * Lấy giỏ hàng của khách hàng
     */
    @Transactional(readOnly = true)
    public CartSummaryDTO getCart(Integer khachHangId) {
        log.info("Getting cart for customer: {}", khachHangId);

        GioHang gioHang = gioHangRepository.findActiveCartWithDetailsByKhachHangId(khachHangId)
                .orElse(null);

        if (gioHang == null || gioHang.getChiTietList() == null || gioHang.getChiTietList().isEmpty()) {
            return CartSummaryDTO.builder()
                    .items(new ArrayList<>())
                    .tongSoLuong(0)
                    .tongTien(BigDecimal.ZERO)
                    .tongTietKiem(BigDecimal.ZERO)
                    .build();
        }

        List<CartItemDTO> items = gioHang.getChiTietList().stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());

        return CartSummaryDTO.fromItems(gioHang.getGioHangId(), items);
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @Transactional
    public CartSummaryDTO addToCart(Integer khachHangId, AddToCartRequest request) {
        log.info("Adding to cart - Customer: {}, Variant: {}, Quantity: {}",
                khachHangId, request.getVariantId(), request.getSoLuong());

        // Kiểm tra khách hàng
        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Kiểm tra variant
        SanPhamChiTiet variant = sanPhamChiTietRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Kiểm tra tồn kho
        if (variant.getSoLuongTon() < request.getSoLuong()) {
            throw new RuntimeException("Sản phẩm không đủ số lượng trong kho");
        }

        // Kiểm tra trạng thái sản phẩm
        if (!variant.isActive()) {
            throw new RuntimeException("Sản phẩm hiện không khả dụng");
        }

        // Lấy hoặc tạo giỏ hàng
        GioHang gioHang = gioHangRepository.findActiveCartByKhachHangId(khachHangId)
                .orElseGet(() -> {
                    GioHang newCart = GioHang.builder()
                            .khachHang(khachHang)
                            .createdAt(LocalDateTime.now())
                            .trangThai((byte) 0)
                            .build();
                    return gioHangRepository.save(newCart);
                });

        // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
        GioHangChiTiet chiTiet = gioHangChiTietRepository
                .findByGioHangIdAndVariantId(gioHang.getGioHangId(), variant.getVariantId())
                .orElse(null);

        if (chiTiet != null) {
            // Cập nhật số lượng
            Integer soLuongMoi = chiTiet.getSoLuong() + request.getSoLuong();
            if (variant.getSoLuongTon() < soLuongMoi) {
                throw new RuntimeException("Sản phẩm không đủ số lượng trong kho");
            }
            chiTiet.setSoLuong(soLuongMoi);
        } else {
            // Thêm mới
            chiTiet = GioHangChiTiet.builder()
                    .gioHang(gioHang)
                    .variant(variant)
                    .soLuong(request.getSoLuong())
                    .donGia(variant.getGiaBan())
                    .build();
        }

        gioHangChiTietRepository.save(chiTiet);

        log.info("Added to cart successfully");
        return getCart(khachHangId);
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    @Transactional
    public CartSummaryDTO updateCartItem(Integer khachHangId, Integer cartItemId, UpdateCartItemRequest request) {
        log.info("Updating cart item: {} - New quantity: {}", cartItemId, request.getSoLuong());

        GioHangChiTiet chiTiet = gioHangChiTietRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        // Kiểm tra quyền sở hữu giỏ hàng
        if (!chiTiet.getGioHang().getKhachHang().getKhachHangId().equals(khachHangId)) {
            throw new RuntimeException("Không có quyền cập nhật giỏ hàng này");
        }

        // Kiểm tra tồn kho
        if (chiTiet.getVariant().getSoLuongTon() < request.getSoLuong()) {
            throw new RuntimeException("Sản phẩm không đủ số lượng trong kho");
        }

        chiTiet.setSoLuong(request.getSoLuong());
        gioHangChiTietRepository.save(chiTiet);

        log.info("Updated cart item successfully");
        return getCart(khachHangId);
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @Transactional
    public CartSummaryDTO removeCartItem(Integer khachHangId, Integer cartItemId) {
        log.info("Removing cart item: {}", cartItemId);

        GioHangChiTiet chiTiet = gioHangChiTietRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        // Kiểm tra quyền sở hữu giỏ hàng
        if (!chiTiet.getGioHang().getKhachHang().getKhachHangId().equals(khachHangId)) {
            throw new RuntimeException("Không có quyền xóa sản phẩm trong giỏ hàng này");
        }

        gioHangChiTietRepository.delete(chiTiet);

        log.info("Removed cart item successfully");
        return getCart(khachHangId);
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @Transactional
    public void clearCart(Integer khachHangId) {
        log.info("Clearing cart for customer: {}", khachHangId);

        GioHang gioHang = gioHangRepository.findActiveCartByKhachHangId(khachHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        gioHangChiTietRepository.deleteAllByGioHangId(gioHang.getGioHangId());

        log.info("Cleared cart successfully");
    }

    /**
     * Lấy số lượng sản phẩm trong giỏ hàng
     */
    @Transactional(readOnly = true)
    public Integer getCartItemCount(Integer khachHangId) {
        return gioHangRepository.countItemsByKhachHangId(khachHangId);
    }

    /**
     * Convert entity sang DTO
     */
    private CartItemDTO convertToCartItemDTO(GioHangChiTiet chiTiet) {
        SanPhamChiTiet variant = chiTiet.getVariant();

        return CartItemDTO.builder()
                .gioHangCTId(chiTiet.getGioHangCTId())
                .variantId(variant.getVariantId())
                .tenSanPham(variant.getSanPham().getTen())  // SanPham.ten
                .sku(variant.getSku())
                .hinhAnh(variant.getHinhAnh())
                .mauSac(variant.getMauSac().getTen())  // MauSac.ten
                .kichThuoc(variant.getKichThuoc().getTen())  // KichThuoc.ten
                .soLuong(chiTiet.getSoLuong())
                .donGia(chiTiet.getDonGia())
                .thanhTien(chiTiet.getThanhTien())
                .soLuongTon(variant.getSoLuongTon())
                .giaGoc(variant.getGiaGoc())
                .build();
    }
}