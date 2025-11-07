package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "Newsletter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Newsletter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NewsletterID")
    private Integer newsletterId;

    @Column(name = "Email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive;

    @Column(name = "SubscribedAt", nullable = false)
    private LocalDateTime subscribedAt;

    @Column(name = "UnsubscribedAt")
    private LocalDateTime unsubscribedAt;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (subscribedAt == null) {
            subscribedAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    // ==================== TRANSIENT FIELDS ====================

    /**
     * Format ngày đăng ký
     */
    @Transient
    public String getSubscribedAtFormatted() {
        if (subscribedAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return subscribedAt.format(formatter);
        }
        return "";
    }

    /**
     * Format ngày hủy đăng ký
     */
    @Transient
    public String getUnsubscribedAtFormatted() {
        if (unsubscribedAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return unsubscribedAt.format(formatter);
        }
        return "";
    }
}