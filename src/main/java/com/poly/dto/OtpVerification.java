package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Integer otpId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "otp_code", nullable = false, length = 6)
    private String otpCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "khach_hang_id")
    private Integer khachHangId;

    @Column(name = "purpose") // EMAIL_CHANGE, PHONE_CHANGE, etc.
    private String purpose;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        expiredAt = createdAt.plusMinutes(5); // OTP hết hạn sau 5 phút
        isUsed = false;
    }
}