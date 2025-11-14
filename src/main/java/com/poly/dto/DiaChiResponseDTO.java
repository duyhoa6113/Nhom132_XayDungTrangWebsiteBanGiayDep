package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiResponseDTO {
    private Integer diaChiId;
    private String hoTenNhan;
    private String sdtNhan;
    private String diaChi;
    private String phuongXa;
    private String quanHuyen;
    private String tinhTP;
    private Boolean macDinh;
    private LocalDateTime createdAt;

    // Constructor từ Entity
    public DiaChiResponseDTO(com.poly.entity.DiaChi diaChi) {
        this.diaChiId = diaChi.getDiaChiId();
        this.hoTenNhan = diaChi.getHoTenNhan();
        this.sdtNhan = diaChi.getSdtNhan();
        this.diaChi = diaChi.getDiaChi();
        this.phuongXa = diaChi.getPhuongXa();
        this.quanHuyen = diaChi.getQuanHuyen();
        this.tinhTP = diaChi.getTinhTP();
        this.macDinh = diaChi.getMacDinh();
        this.createdAt = diaChi.getCreatedAt();
    }

    public String getDiaChiDayDu() {
        StringBuilder sb = new StringBuilder();
        sb.append(diaChi);
        if (phuongXa != null && !phuongXa.isEmpty()) {
            sb.append(", ").append(phuongXa);
        }
        if (quanHuyen != null && !quanHuyen.isEmpty()) {
            sb.append(", ").append(quanHuyen);
        }
        if (tinhTP != null && !tinhTP.isEmpty()) {
            sb.append(", ").append(tinhTP);
        }
        return sb.toString();
    }
}