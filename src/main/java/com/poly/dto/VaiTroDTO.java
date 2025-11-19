package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaiTroDTO {

    @NotNull(message = "ID vai trò không được để trống")
    private Integer vaiTroId;

    @NotBlank(message = "Tên vai trò không được để trống")
    @Size(max = 50, message = "Tên vai trò không được quá 50 ký tự")
    private String tenVaiTro;

    @Size(max = 200, message = "Mô tả không được quá 200 ký tự")
    private String moTa;

    private LocalDateTime createdAt;

    private Integer soLuongNhanVien; // Số lượng nhân viên thuộc vai trò này
}