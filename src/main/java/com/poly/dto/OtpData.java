package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpData {
    private String email;
    private String otpCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private Integer khachHangId;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public boolean isValid(String code) {
        return !isExpired() && otpCode.equals(code);
    }
}