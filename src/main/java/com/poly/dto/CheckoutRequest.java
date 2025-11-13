package com.poly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<Integer> cartItemIds;

    // Sử dụng địa chỉ có sẵn
    private Integer diaChiId;

    // Hoặc tạo địa chỉ mới
    private String hoTenNhan;

    @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    private String sdtNhan;

    private String diaChi;
    private String phuongXa;
    private String quanHuyen;
    private String tinhTP;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String phuongThucThanhToan; // COD, BANK, CARD, MOMO

    private String ghiChu;
    private String maKhuyenMai;
}