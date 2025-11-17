package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDTO {

    private String hoTen;

    private String email;

    private String sdt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    private String gioiTinh;
}