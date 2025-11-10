package com.poly.dto;

/**
 * DTO Interface cho danh mục kèm số lượng sản phẩm
 * Projection interface cho Spring Data JPA
 */
public interface CategoryWithCount {
    Integer getDanhMucId();
    String getTen();
    String getMoTa();
    Long getProductCount();
}