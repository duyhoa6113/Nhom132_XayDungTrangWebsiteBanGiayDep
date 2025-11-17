package com.poly.service;

import com.poly.dto.AddToCartRequest;
import com.poly.dto.CartItemDTO;
import com.poly.dto.UpdateCartRequest;
import com.poly.entity.GioHang;
import com.poly.entity.KhachHang;
import com.poly.entity.SanPhamChiTiet;
import com.poly.repository.GioHangRepository;
import com.poly.repository.SanPhamChiTietRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final GioHangRepository gioHangRepository;
    private final SanPhamChiTietRepository variantRepository;

    /**
     * Lấy danh sách giỏ hàng
     */
    public List<CartItemDTO> getCartItems(KhachHang khachHang) {
        List<GioHang> gioHangList = gioHangRepository
                .findByKhachHangOrderByCreatedAtDesc(khachHang);

        return gioHangList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Thêm vào giỏ hàng
     */
    @Transactional
    public CartItemDTO addToCart(KhachHang khachHang, AddToCartRequest request) {
        // Tìm variant
        SanPhamChiTiet variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Kiểm tra tồn kho
        if (variant.getSoLuongTon() < request.getSoLuong()) {
            throw new RuntimeException("Sản phẩm không đủ số lượng trong kho");
        }

        // Kiểm tra trạng thái
        if (variant.getTrangThai() != 1) {
            throw new RuntimeException("Sản phẩm không khả dụng");
        }

        // Kiểm tra đã có trong giỏ hàng chưa
        GioHang gioHang = gioHangRepository
                .findByKhachHangAndVariant(khachHang, variant)
                .orElse(null);

        if (gioHang != null) {
            // Cập nhật số lượng
            int newQuantity = gioHang.getSoLuong() + request.getSoLuong();

            if (newQuantity > variant.getSoLuongTon()) {
                throw new RuntimeException("Tổng số lượng vượt quá tồn kho");
            }

            gioHang.setSoLuong(newQuantity);
        } else {
            // Tạo mới
            gioHang = new GioHang();
            gioHang.setKhachHang(khachHang);
            gioHang.setVariant(variant);
            gioHang.setSoLuong(request.getSoLuong());
        }

        gioHang = gioHangRepository.save(gioHang);
        log.info("Added/Updated cart item: {}", gioHang.getGioHangId());

        return convertToDTO(gioHang);
    }

    /**
     * Cập nhật số lượng
     */
    @Transactional
    public CartItemDTO updateCartItem(KhachHang khachHang, UpdateCartRequest request) {
        GioHang gioHang = gioHangRepository.findById(request.getCartItemId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        // Kiểm tra quyền sở hữu
        if (!gioHang.getKhachHang().getKhachHangId().equals(khachHang.getKhachHangId())) {
            throw new RuntimeException("Không có quyền thao tác");
        }

        // Kiểm tra tồn kho
        SanPhamChiTiet variant = gioHang.getVariant();
        if (request.getSoLuong() > variant.getSoLuongTon()) {
            throw new RuntimeException("Số lượng vượt quá tồn kho");
        }

        gioHang.setSoLuong(request.getSoLuong());
        gioHang = gioHangRepository.save(gioHang);

        log.info("Updated cart item: {}", gioHang.getGioHangId());

        return convertToDTO(gioHang);
    }

    /**
     * Xóa sản phẩm
     */
    @Transactional
    public void deleteCartItem(KhachHang khachHang, Integer gioHangId) {
        GioHang gioHang = gioHangRepository.findById(gioHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        // Kiểm tra quyền sở hữu
        if (!gioHang.getKhachHang().getKhachHangId().equals(khachHang.getKhachHangId())) {
            throw new RuntimeException("Không có quyền thao tác");
        }

        gioHangRepository.delete(gioHang);
        log.info("Deleted cart item: {}", gioHangId);
    }

    /**
     * Xóa nhiều sản phẩm
     */
    @Transactional
    public void deleteCartItems(KhachHang khachHang, List<Integer> gioHangIds) {
        gioHangRepository.deleteByKhachHangAndGioHangIdIn(khachHang, gioHangIds);
        log.info("Deleted {} cart items", gioHangIds.size());
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @Transactional
    public void clearCart(KhachHang khachHang) {
        gioHangRepository.deleteByKhachHang(khachHang);
        log.info("Cleared cart for customer: {}", khachHang.getKhachHangId());
    }

    /**
     * Đếm số lượng sản phẩm
     */
    public Integer getCartCount(KhachHang khachHang) {
        Integer count = gioHangRepository.sumQuantityByKhachHang(khachHang);
        return count != null ? count : 0;
    }

    /**
     * Convert Entity to DTO
     */
    private CartItemDTO convertToDTO(GioHang gioHang) {
        SanPhamChiTiet variant = gioHang.getVariant();

        CartItemDTO dto = new CartItemDTO();
        dto.setId(gioHang.getGioHangId());
        dto.setVariantId(variant.getVariantId());
        dto.setTenSanPham(variant.getSanPham().getTen());
        dto.setHinhAnh(variant.getHinhAnh());
        dto.setMauSac(variant.getMauSac().getTen());
        dto.setKichThuoc(variant.getKichThuoc().getTen());
        dto.setSoLuong(gioHang.getSoLuong());
        dto.setMaxStock(variant.getSoLuongTon());
        dto.setGia(variant.getGiaBan());
        dto.setGiaGoc(variant.getGiaGoc());
        dto.setTongTien(gioHang.getTongTien());
        dto.setSku(variant.getSKU());

        return dto;
    }
}