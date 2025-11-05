package com.poly.service;

import com.poly.dto.ProductDetailDTO;
import com.poly.entity.SanPham;
import com.poly.entity.SanPhamChiTiet;
import com.poly.repository.SanPhamChiTietRepository;
import com.poly.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductDetailService {

    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    private final DecimalFormat priceFormatter = new DecimalFormat("#,###");

    /**
     * Lấy chi tiết sản phẩm theo ID
     */
    @Transactional(readOnly = true)
    public ProductDetailDTO getProductDetail(Integer sanPhamId) {
        // 1. Lấy thông tin sản phẩm chính
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + sanPhamId));

        // 2. Lấy tất cả variants của sản phẩm
        List<SanPhamChiTiet> variants = sanPhamChiTietRepository.findBySanPhamIdWithDetails(sanPhamId);

        if (variants.isEmpty()) {
            throw new RuntimeException("Sản phẩm không có biến thể nào");
        }

        // 3. Build DTO
        return buildProductDetailDTO(sanPham, variants);
    }

    /**
     * Build ProductDetailDTO từ SanPham và danh sách variants
     */
    private ProductDetailDTO buildProductDetailDTO(SanPham sanPham, List<SanPhamChiTiet> variants) {
        // Tính giá min/max
        BigDecimal giaMin = variants.stream()
                .map(SanPhamChiTiet::getGiaBan)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal giaMax = variants.stream()
                .map(SanPhamChiTiet::getGiaBan)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal giaGocMin = variants.stream()
                .map(SanPhamChiTiet::getGiaGoc)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(null);

        BigDecimal giaGocMax = variants.stream()
                .map(SanPhamChiTiet::getGiaGoc)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(null);

        // Tính tỷ lệ giảm giá
        Integer tyLeGiamGia = 0;
        String tienTietKiem = "0";
        if (giaGocMin != null && giaGocMin.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal giamGia = giaGocMin.subtract(giaMin);
            tyLeGiamGia = giamGia.divide(giaGocMin, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .intValue();
            tienTietKiem = priceFormatter.format(giamGia);
        }

        // Tính tổng số lượng tồn
        int tongSoLuongTon = variants.stream()
                .mapToInt(SanPhamChiTiet::getSoLuongTon)
                .sum();

        // Build list variants DTO
        List<ProductDetailDTO.VariantDTO> variantDTOs = variants.stream()
                .map(this::buildVariantDTO)
                .collect(Collectors.toList());

        // Build list colors và sizes
        List<ProductDetailDTO.ColorDTO> colors = buildColorList(variants);
        List<ProductDetailDTO.SizeDTO> sizes = buildSizeList(variants);

        // Lấy hình ảnh
        String hinhAnhChinh = variants.stream()
                .map(SanPhamChiTiet::getHinhAnh)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        List<String> hinhAnhPhu = variants.stream()
                .map(SanPhamChiTiet::getHinhAnh)
                .filter(Objects::nonNull)
                .distinct()
                .skip(1) // Bỏ qua ảnh đầu tiên (đã dùng làm ảnh chính)
                .collect(Collectors.toList());

        // Build DTO
        return ProductDetailDTO.builder()
                // Thông tin chính
                .sanPhamId(sanPham.getSanPhamId())
                .ten(sanPham.getTen())
                .moTa(sanPham.getMoTa())

                // Danh mục
                .danhMucId(sanPham.getDanhMuc() != null ? sanPham.getDanhMuc().getDanhMucId() : null)
                .danhMucTen(sanPham.getDanhMuc() != null ? sanPham.getDanhMuc().getTen() : null)

                // Thương hiệu
                .thuongHieuId(sanPham.getThuongHieu() != null ? sanPham.getThuongHieu().getThuongHieuId() : null)
                .thuongHieuTen(sanPham.getThuongHieu() != null ? sanPham.getThuongHieu().getTen() : null)

                // Chất liệu
                .chatLieuId(sanPham.getChatLieu() != null ? sanPham.getChatLieu().getChatLieuId() : null)
                .chatLieuTen(sanPham.getChatLieu() != null ? sanPham.getChatLieu().getTen() : null)

                // Giá
                .giaMin(giaMin)
                .giaMax(giaMax)
                .giaGocMin(giaGocMin)
                .giaGocMax(giaGocMax)
                .giaMinFormatted(priceFormatter.format(giaMin))
                .giaMaxFormatted(priceFormatter.format(giaMax))
                .giaGocMinFormatted(giaGocMin != null ? priceFormatter.format(giaGocMin) : null)
                .giaGocMaxFormatted(giaGocMax != null ? priceFormatter.format(giaGocMax) : null)
                .tyLeGiamGia(tyLeGiamGia)
                .tienTietKiem(tienTietKiem)

                // Tồn kho
                .soLuongTon(tongSoLuongTon)

                // Hình ảnh
                .hinhAnhChinh(hinhAnhChinh)
                .hinhAnhPhu(hinhAnhPhu)

                // Variants, colors, sizes
                .variants(variantDTOs)
                .colors(colors)
                .sizes(sizes)

                .build();
    }

    /**
     * Build VariantDTO từ SanPhamChiTiet
     */
    private ProductDetailDTO.VariantDTO buildVariantDTO(SanPhamChiTiet spct) {
        return ProductDetailDTO.VariantDTO.builder()
                .variantId(spct.getVariantId())
                .sku(spct.getSku())
                .barcode(spct.getBarcode())
                .giaBan(spct.getGiaBan())
                .giaGoc(spct.getGiaGoc())
                .giaBanFormatted(priceFormatter.format(spct.getGiaBan()))
                .giaGocFormatted(spct.getGiaGoc() != null ? priceFormatter.format(spct.getGiaGoc()) : null)
                .soLuongTon(spct.getSoLuongTon())
                .hinhAnh(spct.getHinhAnh())
                .mauSacId(spct.getMauSac().getMauSacId())
                .mauSacTen(spct.getMauSac().getTen())
                .kichThuocId(spct.getKichThuoc().getKichThuocId())
                .kichThuocTen(spct.getKichThuoc().getTen())
                .build();
    }

    /**
     * Build danh sách màu sắc có sẵn
     */
    private List<ProductDetailDTO.ColorDTO> buildColorList(List<SanPhamChiTiet> variants) {
        Map<Integer, ProductDetailDTO.ColorDTO> colorMap = new LinkedHashMap<>();

        for (SanPhamChiTiet variant : variants) {
            Integer mauSacId = variant.getMauSac().getMauSacId();

            if (!colorMap.containsKey(mauSacId)) {
                colorMap.put(mauSacId, ProductDetailDTO.ColorDTO.builder()
                        .mauSacId(mauSacId)
                        .ten(variant.getMauSac().getTen())
                        .available(false) // Sẽ update sau
                        .build());
            }

            // Nếu có ít nhất 1 variant còn hàng thì màu này available
            if (variant.getSoLuongTon() > 0) {
                colorMap.get(mauSacId).setAvailable(true);
            }
        }

        return new ArrayList<>(colorMap.values());
    }

    /**
     * Build danh sách kích thước có sẵn
     */
    private List<ProductDetailDTO.SizeDTO> buildSizeList(List<SanPhamChiTiet> variants) {
        Map<Integer, ProductDetailDTO.SizeDTO> sizeMap = new LinkedHashMap<>();

        for (SanPhamChiTiet variant : variants) {
            Integer kichThuocId = variant.getKichThuoc().getKichThuocId();

            if (!sizeMap.containsKey(kichThuocId)) {
                sizeMap.put(kichThuocId, ProductDetailDTO.SizeDTO.builder()
                        .kichThuocId(kichThuocId)
                        .ten(variant.getKichThuoc().getTen())
                        .available(false) // Sẽ update sau
                        .build());
            }

            // Nếu có ít nhất 1 variant còn hàng thì size này available
            if (variant.getSoLuongTon() > 0) {
                sizeMap.get(kichThuocId).setAvailable(true);
            }
        }

        return new ArrayList<>(sizeMap.values());
    }
}