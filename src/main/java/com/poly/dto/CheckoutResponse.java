package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {

    private boolean success;
    private String message;
    private Integer orderId;
    private String orderCode;

    public static CheckoutResponse success(Integer orderId, String orderCode) {
        return new CheckoutResponse(true, "Đặt hàng thành công", orderId, orderCode);
    }

    public static CheckoutResponse error(String message) {
        return new CheckoutResponse(false, message, null, null);
    }
}