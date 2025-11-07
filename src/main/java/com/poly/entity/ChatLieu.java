package com.poly.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity cho bảng ChatLieu
 *
 * @author Nhóm 132
 */
@Entity
@Table(name = "ChatLieu")
public class ChatLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ChatLieuId")
    private Integer chatLieuId;

    @Column(name = "Ten", nullable = false, unique = true, length = 100)
    private String ten;

    @Column(name = "TrangThai", nullable = false)
    private int trangThai = 1;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    // ============================================
    // RELATIONSHIP - Quan hệ ngược với SanPham
    // ============================================
    /**
     * Một chất liệu có thể được sử dụng cho nhiều sản phẩm
     * mappedBy = "chatLieu" phải khớp với field name trong SanPham entity
     */
    @OneToMany(mappedBy = "chatLieu", fetch = FetchType.LAZY)
    private List<SanPham> sanPhams;

    // ============================================
    // LIFECYCLE CALLBACK
    // ============================================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ============================================
    // GETTERS AND SETTERS
    // ============================================

    public Integer getChatLieuId() {
        return chatLieuId;
    }

    public void setChatLieuId(Integer chatLieuId) {
        this.chatLieuId = chatLieuId;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<SanPham> getSanPhams() {
        return sanPhams;
    }

    public void setSanPhams(List<SanPham> sanPhams) {
        this.sanPhams = sanPhams;
    }

    // ============================================
    // EQUALS AND HASHCODE
    // ============================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatLieu chatLieu = (ChatLieu) o;
        return chatLieuId != null && chatLieuId.equals(chatLieu.chatLieuId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // ============================================
    // TO STRING
    // ============================================

    @Override
    public String toString() {
        return "ChatLieu{" +
                "chatLieuId=" + chatLieuId +
                ", ten='" + ten + '\'' +
                ", trangThai=" + trangThai +
                '}';
    }
}