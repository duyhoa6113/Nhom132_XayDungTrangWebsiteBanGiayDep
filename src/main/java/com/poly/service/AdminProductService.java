package com.poly.service;

import com.poly.dto.SanPhamDTO;
import com.poly.dto.SanPhamChiTietDTO;
import com.poly.entity.*;
import com.poly.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý sản phẩm cho admin - CRUD đầy đủ
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminProductService {

    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final DanhMucRepository danhMucRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;

    // ========== PRODUCT CRUD ==========

    /**
     * Lấy tất cả sản phẩm
     */
    @Transactional(readOnly = true)
    public List<SanPhamDTO> getAllProducts() {
        return sanPhamRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả sản phẩm với phân trang
     */
    @Transactional(readOnly = true)
    public Page<SanPhamDTO> getAllProducts(Pageable pageable) {
        Page<SanPham> productPage = sanPhamRepository.findAll(
                org.springframework.data.domain.PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(Sort.Direction.DESC, "createdAt")
                ));
        List<SanPhamDTO> dtoList = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());
    }

    /**
     * Lấy sản phẩm đang hoạt động
     */
    @Transactional(readOnly = true)
    public List<SanPhamDTO> getActiveProducts() {
        return sanPhamRepository.findByTrangThai(1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy sản phẩm đang hoạt động với phân trang
     */
    @Transactional(readOnly = true)
    public Page<SanPhamDTO> getActiveProducts(Pageable pageable) {
        Page<SanPham> productPage = sanPhamRepository.findByTrangThai(
                1,
                org.springframework.data.domain.PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(Sort.Direction.DESC, "createdAt")
                ));
        List<SanPhamDTO> dtoList = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());
    }

    /**
     * Lấy sản phẩm theo trạng thái với phân trang
     */
    @Transactional(readOnly = true)
    public Page<SanPhamDTO> getProductsByStatus(Integer trangThai, Pageable pageable) {
        Page<SanPham> productPage;
        if (trangThai != null) {
            productPage = sanPhamRepository.findByTrangThai(
                    trangThai,
                    org.springframework.data.domain.PageRequest.of(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            Sort.by(Sort.Direction.DESC, "createdAt")
                    ));
        } else {
            productPage = sanPhamRepository.findAll(
                    org.springframework.data.domain.PageRequest.of(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            Sort.by(Sort.Direction.DESC, "createdAt")
                    ));
        }
        List<SanPhamDTO> dtoList = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());
    }

    /**
     * Lấy sản phẩm theo ID
     */
    @Transactional(readOnly = true)
    public SanPhamDTO getProductById(Integer id) {
        return sanPhamRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Tạo mới sản phẩm
     */
    public SanPhamDTO createProduct(SanPhamDTO dto) {
        validateProduct(dto);

        SanPham product = new SanPham();
        product.setTen(dto.getTen());
        product.setMoTa(dto.getMoTa());
        product.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : 1);

        // Set danh mục (bắt buộc)
        DanhMuc danhMuc = danhMucRepository.findById(dto.getDanhMucId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));
        product.setDanhMuc(danhMuc);

        // Set thương hiệu (tùy chọn)
        if (dto.getThuongHieuId() != null) {
            ThuongHieu thuongHieu = thuongHieuRepository.findById(dto.getThuongHieuId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thương hiệu"));
            product.setThuongHieu(thuongHieu);
        }

        // Set chất liệu (tùy chọn)
        if (dto.getChatLieuId() != null) {
            ChatLieu chatLieu = chatLieuRepository.findById(dto.getChatLieuId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chất liệu"));
            product.setChatLieu(chatLieu);
        }

        SanPham saved = sanPhamRepository.save(product);
        log.info("✅ Đã tạo sản phẩm: {}", saved.getTen());

        return convertToDTO(saved);
    }

    /**
     * Cập nhật sản phẩm
     */
    public SanPhamDTO updateProduct(Integer id, SanPhamDTO dto) {
        validateProduct(dto);

        SanPham product = sanPhamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + id));

        product.setTen(dto.getTen());
        product.setMoTa(dto.getMoTa());
        product.setTrangThai(dto.getTrangThai());

        // Update danh mục
        DanhMuc danhMuc = danhMucRepository.findById(dto.getDanhMucId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));
        product.setDanhMuc(danhMuc);

        // Update thương hiệu
        if (dto.getThuongHieuId() != null) {
            ThuongHieu thuongHieu = thuongHieuRepository.findById(dto.getThuongHieuId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thương hiệu"));
            product.setThuongHieu(thuongHieu);
        } else {
            product.setThuongHieu(null);
        }

        // Update chất liệu
        if (dto.getChatLieuId() != null) {
            ChatLieu chatLieu = chatLieuRepository.findById(dto.getChatLieuId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chất liệu"));
            product.setChatLieu(chatLieu);
        } else {
            product.setChatLieu(null);
        }

        SanPham saved = sanPhamRepository.save(product);
        log.info("✅ Đã cập nhật sản phẩm ID: {}", id);

        return convertToDTO(saved);
    }

    /**
     * Xóa sản phẩm (soft delete - chuyển trạng thái = 0)
     */
    public boolean deleteProduct(Integer id) {
        SanPham product = sanPhamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + id));

        // Kiểm tra có đơn hàng đang sử dụng không
        // TODO: Thêm logic kiểm tra đơn hàng nếu cần

        // Soft delete
        product.setTrangThai(0);
        sanPhamRepository.save(product);

        log.info("✅ Đã xóa (soft delete) sản phẩm ID: {}", id);
        return true;
    }

    /**
     * Đếm tổng số sản phẩm
     */
    @Transactional(readOnly = true)
    public long getTotalCount() {
        return sanPhamRepository.count();
    }

    /**
     * Đếm số sản phẩm đang hoạt động
     */
    @Transactional(readOnly = true)
    public long getActiveCount() {
        Long count = sanPhamRepository.countActiveProducts();
        return count != null ? count : 0L;
    }

    // ========== VARIANT CRUD ==========

    /**
     * Tạo mới variant
     */
    public SanPhamChiTietDTO createVariant(SanPhamChiTietDTO dto) {
        validateVariant(dto);

        SanPham sanPham = sanPhamRepository.findById(dto.getSanPhamId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        MauSac mauSac = mauSacRepository.findById(dto.getMauSacId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy màu sắc"));

        KichThuoc kichThuoc = kichThuocRepository.findById(dto.getKichThuocId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kích thước"));

        // Kiểm tra variant đã tồn tại chưa
        var existing = sanPhamChiTietRepository.findBySanPhamAndMauSacAndKichThuoc(sanPham, mauSac, kichThuoc);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Variant với màu sắc và kích thước này đã tồn tại");
        }

        // Kiểm tra SKU unique
        var existingSku = sanPhamChiTietRepository.findBySku(dto.getSku());
        if (existingSku.isPresent()) {
            throw new IllegalArgumentException("SKU đã tồn tại");
        }

        SanPhamChiTiet variant = new SanPhamChiTiet();
        variant.setSanPham(sanPham);
        variant.setMauSac(mauSac);
        variant.setKichThuoc(kichThuoc);
        variant.setSku(dto.getSku());
        variant.setBarcode(dto.getBarcode());
        variant.setGiaBan(dto.getGiaBan());
        variant.setGiaGoc(dto.getGiaGoc());
        variant.setSoLuongTon(dto.getSoLuongTon() != null ? dto.getSoLuongTon() : 0);
        variant.setHinhAnh(dto.getHinhAnh());
        variant.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : 1);

        SanPhamChiTiet saved = sanPhamChiTietRepository.save(variant);
        log.info("✅ Đã tạo variant: {}", saved.getSKU());

        return convertVariantToDTO(saved);
    }

    /**
     * Cập nhật variant
     */
    public SanPhamChiTietDTO updateVariant(Integer id, SanPhamChiTietDTO dto) {
        validateVariant(dto);

        SanPhamChiTiet variant = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy variant với ID: " + id));

        MauSac mauSac = mauSacRepository.findById(dto.getMauSacId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy màu sắc"));

        KichThuoc kichThuoc = kichThuocRepository.findById(dto.getKichThuocId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kích thước"));

        // Kiểm tra variant đã tồn tại chưa (trừ chính nó)
        var existing = sanPhamChiTietRepository.findBySanPhamAndMauSacAndKichThuoc(
                variant.getSanPham(), mauSac, kichThuoc);
        if (existing.isPresent() && !existing.get().getVariantId().equals(id)) {
            throw new IllegalArgumentException("Variant với màu sắc và kích thước này đã tồn tại");
        }

        // Kiểm tra SKU unique (trừ chính nó)
        var existingSku = sanPhamChiTietRepository.findBySku(dto.getSku());
        if (existingSku.isPresent() && !existingSku.get().getVariantId().equals(id)) {
            throw new IllegalArgumentException("SKU đã tồn tại");
        }

        variant.setMauSac(mauSac);
        variant.setKichThuoc(kichThuoc);
        variant.setSku(dto.getSku());
        variant.setBarcode(dto.getBarcode());
        variant.setGiaBan(dto.getGiaBan());
        variant.setGiaGoc(dto.getGiaGoc());
        variant.setSoLuongTon(dto.getSoLuongTon());
        variant.setHinhAnh(dto.getHinhAnh());
        variant.setTrangThai(dto.getTrangThai());

        SanPhamChiTiet saved = sanPhamChiTietRepository.save(variant);
        log.info("✅ Đã cập nhật variant ID: {}", id);

        return convertVariantToDTO(saved);
    }

    /**
     * Xóa variant
     */
    public boolean deleteVariant(Integer id) {
        SanPhamChiTiet variant = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy variant với ID: " + id));

        // Kiểm tra có đơn hàng đang sử dụng không
        // TODO: Thêm logic kiểm tra đơn hàng nếu cần

        // Soft delete
        variant.setTrangThai(0);
        sanPhamChiTietRepository.save(variant);

        log.info("✅ Đã xóa (soft delete) variant ID: {}", id);
        return true;
    }

    /**
     * Lấy danh sách variants của một sản phẩm
     */
    @Transactional(readOnly = true)
    public List<SanPhamChiTietDTO> getVariantsByProductId(Integer productId) {
        SanPham product = sanPhamRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        List<SanPhamChiTiet> variants = sanPhamChiTietRepository.findBySanPham(product);
        return variants.stream()
                .map(this::convertVariantToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy variant theo ID
     */
    @Transactional(readOnly = true)
    public SanPhamChiTietDTO getVariantById(Integer variantId) {
        SanPhamChiTiet variant = sanPhamChiTietRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy variant với ID: " + variantId));
        return convertVariantToDTO(variant);
    }

    // ========== HELPER METHODS ==========

    /**
     * Convert Entity to DTO
     */
    private SanPhamDTO convertToDTO(SanPham product) {
        SanPhamDTO dto = new SanPhamDTO();
        dto.setSanPhamId(product.getSanPhamId());
        dto.setTen(product.getTen());
        dto.setMoTa(product.getMoTa());
        dto.setTrangThai(product.getTrangThai());
        dto.setSoLuongDaBan(product.getSoLuongDaBan());

        if (product.getDanhMuc() != null) {
            dto.setDanhMucId(product.getDanhMuc().getDanhMucId());
            dto.setDanhMucTen(product.getDanhMuc().getTen());
        }

        if (product.getThuongHieu() != null) {
            dto.setThuongHieuId(product.getThuongHieu().getThuongHieuId());
            dto.setThuongHieuTen(product.getThuongHieu().getTen());
        }

        if (product.getChatLieu() != null) {
            dto.setChatLieuId(product.getChatLieu().getChatLieuId());
            dto.setChatLieuTen(product.getChatLieu().getTen());
        }

        // Lấy variants - fetch từ repository để đảm bảo load được
        try {
            List<SanPhamChiTiet> variants = sanPhamChiTietRepository.findBySanPham(product);
            if (variants != null && !variants.isEmpty()) {
                List<SanPhamChiTietDTO> variantDTOs = variants.stream()
                        .map(this::convertVariantToDTO)
                        .collect(Collectors.toList());
                dto.setVariants(variantDTOs);

                // Tính toán thông tin bổ sung từ variants active
                List<SanPhamChiTietDTO> activeVariants = variantDTOs.stream()
                        .filter(v -> v.getTrangThai() != null && v.getTrangThai() == 1)
                        .collect(Collectors.toList());

                if (!activeVariants.isEmpty()) {
                    // Lấy hình ảnh đầu tiên
                    dto.setHinhAnhChinh(activeVariants.stream()
                            .filter(v -> v.getHinhAnh() != null && !v.getHinhAnh().isEmpty())
                            .map(SanPhamChiTietDTO::getHinhAnh)
                            .findFirst()
                            .orElse(null));

                    // Tính tổng số lượng tồn
                    dto.setSoLuongTon(activeVariants.stream()
                            .mapToInt(v -> v.getSoLuongTon() != null ? v.getSoLuongTon() : 0)
                            .sum());

                    // Lấy giá thấp nhất
                    dto.setGiaMin(activeVariants.stream()
                            .map(SanPhamChiTietDTO::getGiaBan)
                            .filter(gia -> gia != null)
                            .mapToDouble(BigDecimal::doubleValue)
                            .min()
                            .orElse(0.0));
                }
            }
        } catch (Exception e) {
            log.warn("Error loading variants for product {}: {}", product.getSanPhamId(), e.getMessage());
        }

        return dto;
    }

    /**
     * Convert Variant Entity to DTO
     */
    private SanPhamChiTietDTO convertVariantToDTO(SanPhamChiTiet variant) {
        SanPhamChiTietDTO dto = new SanPhamChiTietDTO();
        dto.setVariantId(variant.getVariantId());
        dto.setSanPhamId(variant.getSanPham().getSanPhamId());
        dto.setSku(variant.getSKU());
        dto.setBarcode(variant.getBarcode());
        dto.setGiaBan(variant.getGiaBan());
        dto.setGiaGoc(variant.getGiaGoc());
        dto.setSoLuongTon(variant.getSoLuongTon());
        dto.setHinhAnh(variant.getHinhAnh());
        dto.setTrangThai(variant.getTrangThai());

        if (variant.getMauSac() != null) {
            dto.setMauSacId(variant.getMauSac().getMauSacId());
            dto.setMauSacTen(variant.getMauSac().getTen());
            dto.setMauSacMaHex(variant.getMauSac().getMaHex());
        }

        if (variant.getKichThuoc() != null) {
            dto.setKichThuocId(variant.getKichThuoc().getKichThuocId());
            dto.setKichThuocTen(variant.getKichThuoc().getTen());
        }

        return dto;
    }

    /**
     * Validate product
     */
    private void validateProduct(SanPhamDTO dto) {
        if (dto.getTen() == null || dto.getTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (dto.getTen().length() > 200) {
            throw new IllegalArgumentException("Tên sản phẩm không được vượt quá 200 ký tự");
        }
        if (dto.getDanhMucId() == null) {
            throw new IllegalArgumentException("Danh mục là bắt buộc");
        }
        if (dto.getTrangThai() != null && (dto.getTrangThai() < 0 || dto.getTrangThai() > 1)) {
            throw new IllegalArgumentException("Trạng thái chỉ nhận giá trị 0 hoặc 1");
        }
    }

    /**
     * Validate variant
     */
    private void validateVariant(SanPhamChiTietDTO dto) {
        if (dto.getSanPhamId() == null) {
            throw new IllegalArgumentException("Sản phẩm là bắt buộc");
        }
        if (dto.getMauSacId() == null) {
            throw new IllegalArgumentException("Màu sắc là bắt buộc");
        }
        if (dto.getKichThuocId() == null) {
            throw new IllegalArgumentException("Kích thước là bắt buộc");
        }
        if (dto.getSku() == null || dto.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("SKU không được để trống");
        }
        if (dto.getGiaBan() == null || dto.getGiaBan().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá bán phải lớn hơn hoặc bằng 0");
        }
        if (dto.getSoLuongTon() == null || dto.getSoLuongTon() < 0) {
            throw new IllegalArgumentException("Số lượng tồn phải lớn hơn hoặc bằng 0");
        }
    }
}

