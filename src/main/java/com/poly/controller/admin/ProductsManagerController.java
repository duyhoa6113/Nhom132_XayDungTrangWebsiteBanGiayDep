package com.poly.controller.admin;

import com.poly.dto.SanPhamDTO;
import com.poly.dto.SanPhamChiTietDTO;
import com.poly.entity.*;
import com.poly.service.AdminProductService;
import com.poly.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý sản phẩm - CRUD đầy đủ
 */
@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Slf4j
public class ProductsManagerController {

    private final AdminProductService adminProductService;
    private final CategoryService categoryService;

    /**
     * Hiển thị trang quản lý sản phẩm với phân trang
     */
    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(value = "trangThai", required = false) Integer trangThai,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        try {
            // Giới hạn size hợp lý
            if (size < 5) size = 5;
            if (size > 100) size = 100;
            
            Pageable pageable = PageRequest.of(page, size);
            Page<SanPhamDTO> productsPage;
            
            // Lọc theo trạng thái
            if (trangThai != null) {
                productsPage = adminProductService.getProductsByStatus(trangThai, pageable);
            } else {
                productsPage = adminProductService.getAllProducts(pageable);
            }

            // Lấy danh sách lookup cho form
            List<DanhMuc> categories = categoryService.getAllActiveCategories();
            List<ThuongHieu> brands = categoryService.getAllActiveBrands();
            List<ChatLieu> materials = categoryService.getAllActiveMaterials();
            List<MauSac> colors = categoryService.getAllActiveColors();
            List<KichThuoc> sizeList = categoryService.getAllActiveSizes();

            // Thống kê
            long totalCount = adminProductService.getTotalCount();
            long activeCount = adminProductService.getActiveCount();

            model.addAttribute("products", productsPage.getContent());
            model.addAttribute("productsPage", productsPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productsPage.getTotalPages());
            model.addAttribute("totalElements", productsPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("categories", categories);
            model.addAttribute("brands", brands);
            model.addAttribute("materials", materials);
            model.addAttribute("colors", colors);
            model.addAttribute("sizes", sizeList);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("activeCount", activeCount);
            model.addAttribute("selectedTrangThai", trangThai);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", "products");

        } catch (Exception e) {
            log.error("Error loading products: {}", e.getMessage(), e);
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("products", List.of());
            model.addAttribute("totalCount", 0L);
            model.addAttribute("activeCount", 0L);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
        }

        return "admin/products/index";
    }

    /**
     * Thêm mới sản phẩm
     */
    @PostMapping("/add")
    public String add(
            @RequestParam("ten") String ten,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("danhMucId") Integer danhMucId,
            @RequestParam(value = "thuongHieuId", required = false) Integer thuongHieuId,
            @RequestParam(value = "chatLieuId", required = false) Integer chatLieuId,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        try {
            SanPhamDTO dto = new SanPhamDTO();
            dto.setTen(ten);
            dto.setMoTa(moTa);
            dto.setDanhMucId(danhMucId);
            dto.setThuongHieuId(thuongHieuId);
            dto.setChatLieuId(chatLieuId);
            dto.setTrangThai(trangThai != null ? trangThai : 1);

            SanPhamDTO saved = adminProductService.createProduct(dto);
            log.info("✅ Đã thêm sản phẩm ID: {}", saved.getSanPhamId());

            redirectAttributes.addFlashAttribute("success", "✅ Thêm sản phẩm thành công!");
            
        } catch (Exception e) {
            log.error("❌ Lỗi khi thêm sản phẩm: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    /**
     * Cập nhật sản phẩm
     */
    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Integer id,
            @RequestParam("ten") String ten,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("danhMucId") Integer danhMucId,
            @RequestParam(value = "thuongHieuId", required = false) Integer thuongHieuId,
            @RequestParam(value = "chatLieuId", required = false) Integer chatLieuId,
            @RequestParam("trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes) {

        try {
            SanPhamDTO dto = new SanPhamDTO();
            dto.setTen(ten);
            dto.setMoTa(moTa);
            dto.setDanhMucId(danhMucId);
            dto.setThuongHieuId(thuongHieuId);
            dto.setChatLieuId(chatLieuId);
            dto.setTrangThai(trangThai);

            adminProductService.updateProduct(id, dto);
            log.info("✅ Đã cập nhật sản phẩm ID: {}", id);

            redirectAttributes.addFlashAttribute("success", "✅ Cập nhật sản phẩm thành công!");
            
        } catch (Exception e) {
            log.error("❌ Lỗi khi cập nhật sản phẩm: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    /**
     * Xóa sản phẩm
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean deleted = adminProductService.deleteProduct(id);
            response.put("success", deleted);
            response.put("message", deleted ? "Xóa sản phẩm thành công" : "Không thể xóa");

        } catch (Exception e) {
            log.error("❌ Lỗi khi xóa sản phẩm: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * API: Lấy danh sách sản phẩm (JSON)
     */
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getList(
            @RequestParam(required = false) Integer trangThai) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<SanPhamDTO> products = (trangThai != null && trangThai == 1)
                    ? adminProductService.getActiveProducts()
                    : adminProductService.getAllProducts();

            response.put("success", true);
            response.put("data", products);
            response.put("total", products.size());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * API: Lấy sản phẩm theo ID (JSON)
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            SanPhamDTO product = adminProductService.getProductById(id);

            if (product != null) {
                response.put("success", true);
                response.put("data", product);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy sản phẩm");
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    // ========== VARIANT (SanPhamChiTiet) CRUD ==========

    /**
     * Thêm variant cho sản phẩm
     */
    @PostMapping("/variants/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addVariant(
            @RequestParam("sanPhamId") Integer sanPhamId,
            @RequestParam("mauSacId") Integer mauSacId,
            @RequestParam("kichThuocId") Integer kichThuocId,
            @RequestParam("sku") String sku,
            @RequestParam(value = "barcode", required = false) String barcode,
            @RequestParam("giaBan") java.math.BigDecimal giaBan,
            @RequestParam(value = "giaGoc", required = false) java.math.BigDecimal giaGoc,
            @RequestParam("soLuongTon") Integer soLuongTon,
            @RequestParam(value = "hinhAnh", required = false) String hinhAnh,
            @RequestParam("trangThai") Integer trangThai) {

        Map<String, Object> response = new HashMap<>();

        try {
            SanPhamChiTietDTO dto = new SanPhamChiTietDTO();
            dto.setSanPhamId(sanPhamId);
            dto.setMauSacId(mauSacId);
            dto.setKichThuocId(kichThuocId);
            dto.setSku(sku);
            dto.setBarcode(barcode);
            dto.setGiaBan(giaBan);
            dto.setGiaGoc(giaGoc);
            dto.setSoLuongTon(soLuongTon);
            dto.setHinhAnh(hinhAnh);
            dto.setTrangThai(trangThai != null ? trangThai : 1);

            SanPhamChiTietDTO saved = adminProductService.createVariant(dto);
            response.put("success", true);
            response.put("message", "Thêm variant thành công");
            response.put("data", saved);

        } catch (Exception e) {
            log.error("❌ Lỗi khi thêm variant: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật variant
     */
    @PostMapping("/variants/update/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateVariant(
            @PathVariable Integer id,
            @RequestParam("mauSacId") Integer mauSacId,
            @RequestParam("kichThuocId") Integer kichThuocId,
            @RequestParam("sku") String sku,
            @RequestParam(value = "barcode", required = false) String barcode,
            @RequestParam("giaBan") java.math.BigDecimal giaBan,
            @RequestParam(value = "giaGoc", required = false) java.math.BigDecimal giaGoc,
            @RequestParam("soLuongTon") Integer soLuongTon,
            @RequestParam(value = "hinhAnh", required = false) String hinhAnh,
            @RequestParam("trangThai") Integer trangThai) {

        Map<String, Object> response = new HashMap<>();

        try {
            SanPhamChiTietDTO dto = new SanPhamChiTietDTO();
            dto.setMauSacId(mauSacId);
            dto.setKichThuocId(kichThuocId);
            dto.setSku(sku);
            dto.setBarcode(barcode);
            dto.setGiaBan(giaBan);
            dto.setGiaGoc(giaGoc);
            dto.setSoLuongTon(soLuongTon);
            dto.setHinhAnh(hinhAnh);
            dto.setTrangThai(trangThai);

            adminProductService.updateVariant(id, dto);
            response.put("success", true);
            response.put("message", "Cập nhật variant thành công");

        } catch (Exception e) {
            log.error("❌ Lỗi khi cập nhật variant: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Xóa variant
     */
    @PostMapping("/variants/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteVariant(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean deleted = adminProductService.deleteVariant(id);
            response.put("success", deleted);
            response.put("message", deleted ? "Xóa variant thành công" : "Không thể xóa");

        } catch (Exception e) {
            log.error("❌ Lỗi khi xóa variant: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * API: Lấy danh sách variants của một sản phẩm
     */
    @GetMapping("/variants/list/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getVariantsByProductId(@PathVariable Integer productId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SanPhamChiTietDTO> variants = adminProductService.getVariantsByProductId(productId);
            response.put("success", true);
            response.put("data", variants);
            response.put("total", variants.size());

        } catch (Exception e) {
            log.error("❌ Lỗi khi lấy variants: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * API: Lấy variant theo ID
     */
    @GetMapping("/variants/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getVariantById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            SanPhamChiTietDTO variant = adminProductService.getVariantById(id);
            response.put("success", true);
            response.put("data", variant);

        } catch (Exception e) {
            log.error("❌ Lỗi khi lấy variant: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}

