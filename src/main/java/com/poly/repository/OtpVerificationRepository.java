package com.poly.repository;

import com.poly.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Integer> {

    Optional<OtpVerification> findByEmailAndOtpCodeAndIsUsedFalseAndExpiredAtAfter(
            String email, String otpCode, LocalDateTime now);

    List<OtpVerification> findByEmailAndIsUsedFalse(String email);

    void deleteByExpiredAtBefore(LocalDateTime now);
}