package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "KhuyenMai_Variant", schema = "dbo",
        uniqueConstraints = @UniqueConstraint(name="UQ_KM_Variant", columnNames = {"KhuyenMaiId","VariantId"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KhuyenMaiVariant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne(optional = false) @JoinColumn(name = "KhuyenMaiId")
    private KhuyenMai khuyenMai;

    @ManyToOne(optional = false) @JoinColumn(name = "VariantId")
    private SanPhamChiTiet variant;
}

