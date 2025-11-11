package com.poly.service;

import com.poly.entity.SanPham;
import com.poly.entity.SanPhamChiTiet;
import com.poly.entity.ThuongHieu;
import com.poly.repository.SanPhamRepository;
import com.poly.repository.ThuongHieuRepository;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    /**
     * Tìm kiếm sản phẩm với filters
     */
    public Page<SanPham> searchProducts(
            String keyword,
            Integer categoryId,
            List<Integer> brandIds,
            String priceRange,
            Integer rating,
            Pageable pageable
    ) {
        Specification<SanPham> spec = createSearchSpecification(
                keyword, categoryId, brandIds, priceRange, rating
        );

        return sanPhamRepository.findAll(spec, pageable);
    }

    /**
     * Search + sort theo giá tăng/giảm
     */
    public Page<SanPham> searchProductsSortedByPrice(
            String keyword,
            Integer category,
            List<Integer> brand,
            String priceRange,
            Integer rating,
            boolean asc,
            int page,
            int size
    ) {
        // 1. Lấy tất cả sản phẩm thỏa filter (tạm fetch tất cả để sort)
        Pageable fetchAll = PageRequest.of(0, Integer.MAX_VALUE);
        Page<SanPham> allProducts = searchProducts(keyword, category, brand, priceRange, rating, fetchAll);

        // 2. Sắp xếp theo giá (getGiaMin)
        List<SanPham> sortedList = allProducts.getContent().stream()
                .sorted(asc
                        ? Comparator.comparing(SanPham::getGiaMin)
                        : Comparator.comparing(SanPham::getGiaMin).reversed())
                .collect(Collectors.toList());

        // 3. Paging thủ công
        int start = Math.min(page * size, sortedList.size());
        int end = Math.min(start + size, sortedList.size());
        List<SanPham> pageContent = sortedList.subList(start, end);

        return new PageImpl<>(pageContent,
                PageRequest.of(page, size),
                sortedList.size());
    }

    /**
     * Tạo Specification cho tìm kiếm
     */
    private Specification<SanPham> createSearchSpecification(
            String keyword,
            Integer categoryId,
            List<Integer> brandIds,
            String priceRange,
            Integer rating
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Chỉ lấy sản phẩm đang hoạt động
            predicates.add(cb.equal(root.get("trangThai"), 1));

            // 2. Tìm kiếm theo keyword (tên sản phẩm hoặc tên thương hiệu)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                Predicate productName = cb.like(cb.lower(root.get("ten")), pattern);
                Predicate brandName = cb.like(cb.lower(root.get("thuongHieu").get("ten")), pattern);
                predicates.add(cb.or(productName, brandName));
            }

            // 3. Lọc theo danh mục
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("danhMuc").get("danhMucId"), categoryId));
            }

            // 4. Lọc theo thương hiệu
            if (brandIds != null && !brandIds.isEmpty()) {
                predicates.add(root.get("thuongHieu").get("thuongHieuId").in(brandIds));
            }

            // 5. Lọc theo khoảng giá (bằng subquery với variants)
            if (priceRange != null && !priceRange.isEmpty()) {
                addPriceFilter(root, query, cb, predicates, priceRange);
            }

            // 6. Tránh duplicate
            if (query != null) {
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Thêm filter theo khoảng giá
     */
    private void addPriceFilter(Root<SanPham> root, CriteriaQuery<?> query,
                                CriteriaBuilder cb, List<Predicate> predicates,
                                String priceRange) {
        String[] prices = priceRange.split("-");
        if (prices.length == 2) {
            try {
                BigDecimal minPrice = new BigDecimal(prices[0]);
                BigDecimal maxPrice = new BigDecimal(prices[1]);

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
                // Ignore invalid price range
            }
        }
    }

    /**
     * Lấy danh sách thương hiệu từ kết quả tìm kiếm
     */
    public List<ThuongHieu> getBrandsFromSearchResults(String keyword) {
        return thuongHieuRepository.findBrandsByKeyword(keyword);
    }

    /**
     * Autocomplete gợi ý từ khóa
     */
    public List<String> getSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().length() < 2) {
            return List.of();
        }

        List<SanPham> products = sanPhamRepository.findTop10ByTenContainingIgnoreCaseAndTrangThai(keyword, 1);

        return products.stream()
                .map(SanPham::getTen)
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Gợi ý từ khóa liên quan nếu không có kết quả
     */
    public List<String> getSuggestedKeywords(String keyword) {
        return List.of(
                "Giày thể thao",
                "Giày sneaker",
                "Giày nam",
                "Giày nữ"
        );
    }

    /**
     * Đếm số kết quả tìm kiếm
     */
    public long countSearchResults(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return 0;
        }
        return sanPhamRepository.count(createSearchSpecification(keyword, null, null, null, null));
    }
}
