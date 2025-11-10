package com.poly.service;

import com.poly.entity.SanPham;
import com.poly.entity.SanPhamChiTiet;
import com.poly.repository.SanPhamRepository;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
     * Lọc sản phẩm với nhiều tiêu chí
     * FIXED: Sử dụng Specification đúng cách
     */
    public Page<SanPham> filterProducts(
            Integer categoryId,
            List<Integer> brandIds,
            String priceRange,
            List<Integer> sizeIds,
            List<Integer> colorIds,
            List<Integer> materialIds,
            Integer rating,
            Pageable pageable
    ) {
        Specification<SanPham> spec = createSpecification(
                categoryId, brandIds, priceRange, sizeIds,
                colorIds, materialIds, rating
        );

        return sanPhamRepository.findAll(spec, pageable);
    }

    /**
     * Tạo Specification cho filter
     */
    private Specification<SanPham> createSpecification(
            Integer categoryId,
            List<Integer> brandIds,
            String priceRange,
            List<Integer> sizeIds,
            List<Integer> colorIds,
            List<Integer> materialIds,
            Integer rating
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
     * Lấy sản phẩm theo danh mục (không filter)
     */
    public Page<SanPham> getProductsByCategory(Integer categoryId, Pageable pageable) {
        return sanPhamRepository.findProductsByCategoryWithPage(categoryId, pageable);
    }

    /**
     * Search sản phẩm theo tên
     */
    public Page<SanPham> searchProducts(String keyword, Pageable pageable) {
        return sanPhamRepository.searchProducts(keyword, pageable);
    }

    /**
     * Lấy sản phẩm nổi bật
     */
    public List<SanPham> getFeaturedProducts(int limit) {
        return sanPhamRepository.findFeaturedProducts(
                org.springframework.data.domain.PageRequest.of(0, limit)
        );
    }
}