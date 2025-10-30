package com.poly.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ThuongHieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThuongHieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ThuongHieuId")
    private Integer thuongHieuId;

    @Column(name = "Ten", length = 150, nullable = false, unique = true)
    private String ten;

    @Column(name = "MoTa", length = 1000)
    private String moTa;

    @Column(name = "TrangThai", nullable = false)
    private Byte trangThai = 1;
}