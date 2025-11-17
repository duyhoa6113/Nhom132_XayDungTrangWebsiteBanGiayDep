package com.poly.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @Size(min = 2, max = 150, message = "Họ tên phải từ 2-150 ký tự")
    private String hoTen;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String sdt;

    private LocalDate ngaySinh;

    private String gioiTinh;
}