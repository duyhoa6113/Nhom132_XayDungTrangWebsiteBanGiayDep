package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ChatLieu", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ChatLieuId")
    private Integer chatLieuId;

    @Column(name = "Ten", length = 100, nullable = false, unique = true)
    private String ten;
}