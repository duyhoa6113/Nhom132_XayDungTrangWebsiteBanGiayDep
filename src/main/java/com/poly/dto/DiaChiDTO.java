package com.poly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiDTO {

    private Integer diaChiId;

    @NotBlank(message = "Họ tên người nhận không được để trống")
    @Size(min = 2, max = 150, message = "Họ tên phải từ 2-150 ký tự")
    private String hoTenNhan;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String sdtNhan;

    @Size(max = 150, message = "Tên tỉnh/thành phố không quá 150 ký tự")
    private String tinhTP;

    @Size(max = 150, message = "Tên quận/huyện không quá 150 ký tự")
    private String quanHuyen;

    @Size(max = 150, message = "Tên phường/xã không quá 150 ký tự")
    private String phuongXa;

    @NotBlank(message = "Địa chỉ cụ thể không được để trống")
    @Size(min = 5, max = 500, message = "Địa chỉ phải từ 5-500 ký tự")
    private String diaChi;

    private Boolean macDinh = false;

    // Thêm khachHangId nếu cần
    private Integer khachHangId;

    /**
     * Lấy địa chỉ đầy đủ (formatted)
     */
    public String getDiaChiDayDu() {
        StringBuilder sb = new StringBuilder();

        if (diaChi != null && !diaChi.trim().isEmpty()) {
            sb.append(diaChi.trim());
        }

        if (phuongXa != null && !phuongXa.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(phuongXa.trim());
        }

        if (quanHuyen != null && !quanHuyen.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(quanHuyen.trim());
        }

        if (tinhTP != null && !tinhTP.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(tinhTP.trim());
        }

        return sb.toString();
    }

    /**
     * Kiểm tra địa chỉ có hợp lệ không
     */
    public boolean isValid() {
        return hoTenNhan != null && !hoTenNhan.trim().isEmpty()
                && sdtNhan != null && sdtNhan.matches("^[0-9]{10,11}$")
                && diaChi != null && !diaChi.trim().isEmpty();
    }
}