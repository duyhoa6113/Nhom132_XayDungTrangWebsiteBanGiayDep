package com.poly.dto;

import lombok.Data;

@Data
public class MoMoPaymentResponse {
    private String partnerCode;
    private String requestId;
    private String orderId;
    private long amount;
    private long responseTime;
    private String message;
    private int resultCode;
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
}