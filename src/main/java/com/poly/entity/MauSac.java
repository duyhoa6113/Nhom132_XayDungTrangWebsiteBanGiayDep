package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MauSac", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MauSacId")
    private Integer mauSacId;

    @Column(name = "Ten", length = 50, nullable = false, unique = true)
    private String ten;
}