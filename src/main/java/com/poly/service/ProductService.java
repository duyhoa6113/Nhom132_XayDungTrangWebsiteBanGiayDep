package com.poly.service;

import com.poly.entity.SanPham;
import com.poly.entity.SanPhamChiTiet;
import com.poly.repository.SanPhamRepository;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    /**
     * ✅ FIXED: Lọc sản phẩm - trả về Page<SanPham>
     */
    public Page<SanPham> filterProducts(
            Integer categoryId,
            List<Integer> brandIds,
            String priceRange,
            List<Integer> sizeIds,
            List<Integer> colorIds,
            List<Integer> materialIds,
            Integer rating,  // Tham số này sẽ bị bỏ qua
            Pageable pageable
    ) {
        Specification<SanPham> spec = createSpecification(
                categoryId, brandIds, priceRange, sizeIds,
                colorIds, materialIds
        );

        return sanPhamRepository.findAll(spec, pageable);
    }

    /**
     * Tạo Specification cho filter (KHÔNG CÓ RATING)
     */
    private Specification<SanPham> createSpecification(
            Integer categoryId,
            List<Integer> brandIds,
            String priceRange,
            List<Integer> sizeIds,
            List<Integer> colorIds,
            List<Integer> materialIds
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo danh mục
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("danhMuc").get("danhMucId"), categoryId
                ));
            }

            // 2. Chỉ lấy sản phẩm đang hoạt động
            predicates.add(criteriaBuilder.equal(root.get("trangThai"), 1));

            // 3. Lọc theo thương hiệu
            if (brandIds != null && !brandIds.isEmpty()) {
                predicates.add(root.get("thuongHieu").get("thuongHieuId").in(brandIds));
            }

            // 4. Lọc theo khoảng giá
            if (priceRange != null && !priceRange.isEmpty()) {
                addPriceFilter(root, query, criteriaBuilder, predicates, priceRange);
            }

            // 5. Lọc theo size
            if (sizeIds != null && !sizeIds.isEmpty()) {
                addSizeFilter(root, query, criteriaBuilder, predicates, sizeIds);
            }

            // 6. Lọc theo màu sắc
            if (colorIds != null && !colorIds.isEmpty()) {
                addColorFilter(root, query, criteriaBuilder, predicates, colorIds);
            }

            // 7. Lọc theo chất liệu
            if (materialIds != null && !materialIds.isEmpty()) {
                predicates.add(root.get("chatLieu").get("chatLieuId").in(materialIds));
            }

            // 8. Distinct để tránh duplicate khi join
            if (query != null) {
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Thêm filter theo giá
     */
    private void addPriceFilter(Root<SanPham> root, CriteriaQuery<?> query,
                                CriteriaBuilder cb, List<Predicate> predicates,
                                String priceRange) {
        String[] prices = priceRange.split("-");
        if (prices.length == 2) {
            try {
                BigDecimal minPrice = new BigDecimal(prices[0]);
                BigDecimal maxPrice = new BigDecimal(prices[1]);

                // Subquery để check giá trong variants
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<SanPham> subRoot = subquery.from(SanPham.class);
                Join<SanPham, SanPhamChiTiet> variantJoin = subRoot.join("variants");

                subquery.select(cb.count(subRoot.get("sanPhamId")))
                        .where(
                                cb.equal(subRoot.get("sanPhamId"), root.get("sanPhamId")),
                                cb.between(variantJoin.get("giaBan"), minPrice, maxPrice),
                                cb.equal(variantJoin.get("trangThai"), 1)
                        );

                predicates.add(cb.greaterThan(subquery, 0L));
            } catch (NumberFormatException e) {
                // Ignore invalid price format
            }
        }
    }

    /**
     * Thêm filter theo size
     */
    private void addSizeFilter(Root<SanPham> root, CriteriaQuery<?> query,
                               CriteriaBuilder cb, List<Predicate> predicates,
                               List<Integer> sizeIds) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<SanPham> subRoot = subquery.from(SanPham.class);
        Join<SanPham, SanPhamChiTiet> variantJoin = subRoot.join("variants");

        subquery.select(cb.count(subRoot.get("sanPhamId")))
                .where(
                        cb.equal(subRoot.get("sanPhamId"), root.get("sanPhamId")),
                        variantJoin.get("kichThuoc").get("kichThuocId").in(sizeIds),
                        cb.equal(variantJoin.get("trangThai"), 1)
                );

        predicates.add(cb.greaterThan(subquery, 0L));
    }

    /**
     * Thêm filter theo màu sắc
     */
    private void addColorFilter(Root<SanPham> root, CriteriaQuery<?> query,
                                CriteriaBuilder cb, List<Predicate> predicates,
                                List<Integer> colorIds) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<SanPham> subRoot = subquery.from(SanPham.class);
        Join<SanPham, SanPhamChiTiet> variantJoin = subRoot.join("variants");

        subquery.select(cb.count(subRoot.get("sanPhamId")))
                .where(
                        cb.equal(subRoot.get("sanPhamId"), root.get("sanPhamId")),
                        variantJoin.get("mauSac").get("mauSacId").in(colorIds),
                        cb.equal(variantJoin.get("trangThai"), 1)
                );

        predicates.add(cb.greaterThan(subquery, 0L));
    }

    /**
     * Đếm số sản phẩm theo danh mục
     */
    public long countProductsByCategory(Integer categoryId) {
        return sanPhamRepository.countByDanhMucIdAndTrangThai(categoryId, 1);
    }

    /**
     * Tìm sản phẩm theo ID
     */
    public SanPham findById(Integer id) {
        return sanPhamRepository.findById(id)
                .filter(sp -> sp.getTrangThai() == 1)
                .orElse(null);
    }

    /**
     * ✅ FIXED: Lấy sản phẩm theo danh mục - TRẢ VỀ Page
     */
    public Page<SanPham> getProductsByCategory(Integer categoryId, Pageable pageable) {
        return sanPhamRepository.findProductsByCategoryWithPage(categoryId, pageable);
    }

    /**
     * ✅ Search sản phẩm theo tên - TRẢ VỀ Page
     */
    public Page<SanPham> searchProducts(String keyword, Pageable pageable) {
        return sanPhamRepository.searchProducts(keyword, pageable);
    }

    /**
     * ✅ FIXED: Lấy sản phẩm mới nhất - TRẢ VỀ Page
     */
    public Page<SanPham> getNewestProducts(Pageable pageable) {
        return sanPhamRepository.findNewestProducts(pageable);
    }

    /**
     * ✅ FIXED: Lấy sản phẩm nổi bật - TRẢ VỀ List
     * Dùng cho homepage carousel/slider với số lượng cố định
     */
    public List<SanPham> getFeaturedProducts(int limit) {
        return sanPhamRepository.findFeaturedProducts(
                PageRequest.of(0, limit)
        );
    }

    /**
     * ✅ Lấy sản phẩm có discount cao - TRẢ VỀ List
     */
    public List<SanPham> getTopDiscountedProducts(int limit) {
        return sanPhamRepository.findTopDiscountedProducts(
                PageRequest.of(0, limit)
        );
    }

    /**
     * ✅ Lấy sản phẩm random (bán chạy giả lập) - TRẢ VỀ List
     */
    public List<SanPham> getRandomProducts(int limit) {
        return sanPhamRepository.findRandomProducts(limit);
    }

    /**
     * ✅ Lấy sản phẩm liên quan - TRẢ VỀ Page
     */
    public Page<SanPham> getRelatedProducts(Integer categoryId, Integer excludeId, Pageable pageable) {
        return sanPhamRepository.findByDanhMuc_DanhMucIdAndSanPhamIdNotAndTrangThai(
                categoryId, excludeId, 1, pageable
        );
    }

    /**
     * ✅ Lấy sản phẩm khác (fallback khi không đủ sản phẩm liên quan) - TRẢ VỀ Page
     */
    public Page<SanPham> getOtherProducts(Integer excludeId, Pageable pageable) {
        return sanPhamRepository.findBySanPhamIdNotAndTrangThai(
                excludeId, 1, pageable
        );
    }
}