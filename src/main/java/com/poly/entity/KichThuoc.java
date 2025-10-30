package com.poly.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "KichThuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KichThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KichThuocId")
    private Integer kichThuocId;

    @Column(name = "Ten", length = 50, nullable = false, unique = true)
    private String ten;
}