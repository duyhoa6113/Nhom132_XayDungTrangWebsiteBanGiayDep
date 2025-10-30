package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "KhuyenMai", schema = "dbo")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KhuyenMai {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KhuyenMaiId")
    private Integer id;

    @Column(name = "MaCode", length = 50, nullable = false, unique = true)
    private String maCode;

    @Column(name = "Ten", length = 200, nullable = false)
    private String ten;

    @Column(name = "Loai", length = 10, nullable = false) // percent|fixed
    private String loai;

    @Column(name = "GiaTri", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaTri;

    @Column(name = "ToiThieu", precision = 18, scale = 2, nullable = false)
    private BigDecimal toiThieu = BigDecimal.ZERO;

    @Column(name = "NgayBatDau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "NgayKetThuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "TrangThai", nullable = false)
    private Short trangThai = 1;
}

