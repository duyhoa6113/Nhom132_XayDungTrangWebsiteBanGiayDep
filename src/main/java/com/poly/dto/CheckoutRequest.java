package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    private Integer addressId;
    private String paymentMethod;
    private List<Integer> cartItemIds;
    private BigDecimal shippingFee;
    private String note;
}