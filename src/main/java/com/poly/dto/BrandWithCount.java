package com.poly.dto;

/**
 * DTO Interface cho thương hiệu kèm số lượng sản phẩm
 * Projection interface cho Spring Data JPA
 */
public interface BrandWithCount {
    Integer getThuongHieuId();
    String getTen();
    Long getProductCount();
}