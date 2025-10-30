package com.poly.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "DanhMuc", schema = "dbo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DanhMucId")
    private Integer danhMucId;

    @Column(name = "Ten", length = 150, nullable = false, unique = true)
    private String ten;

    @Column(name = "MoTa", length = 1000)
    private String moTa;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai = 1;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "danhMuc", fetch = FetchType.LAZY)
    private List<SanPham> sanPhamList;
}