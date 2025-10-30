package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Table(name = "NhanVien", schema = "dbo")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class NhanVien {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NhanVienId")
    private Integer id;

    @Column(name = "HoTen", length = 150, nullable = false)
    private String hoTen;

    @Column(name = "Email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "Sdt", length = 20)
    private String sdt;

    @Column(name = "VaiTro", length = 50, nullable = false)
    private String vaiTro;

    @Column(name = "TrangThai", nullable = false)
    private Short trangThai = 1;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;
}

