package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "VaiTro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaiTro {

    @Id
    @Column(name = "VaiTroId")
    private Integer vaiTroId;

    @Column(name = "TenVaiTro", nullable = false, unique = true, length = 50)
    private String tenVaiTro;

    @Column(name = "MoTa", length = 200)
    private String moTa;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "vaiTro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NhanVien> nhanViens;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
