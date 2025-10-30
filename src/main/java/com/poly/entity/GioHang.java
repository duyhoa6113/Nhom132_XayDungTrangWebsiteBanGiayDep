package com.poly.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "GioHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GioHangId")
    private Integer gioHangId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KhachHangId", nullable = false)
    private KhachHang khachHang;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai = 0;  // 0: mở, 1: đã đặt hàng

    @OneToMany(mappedBy = "gioHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GioHangChiTiet> chiTietList;
}