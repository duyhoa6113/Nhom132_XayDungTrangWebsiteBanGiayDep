package com.poly.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
}
