package com.poly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String role; // "admin", "employee", "customer"
    private String roleId; // ID vai trò từ VaiTro table (chỉ cho nhân viên)
    private String roleName; // Tên vai trò chi tiết
    private String status; // "active", "inactive"
    private String address;
    private String notes; // Chức vụ cho nhân viên
    private LocalDate dateOfBirth;
    private String gender;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor cho Nhân Viên
    public UserDTO(Integer id, String name, String email, String phone,
                   Integer vaiTroId, String vaiTroName, String chucVu,
                   String diaChi, LocalDate ngaySinh, String gioiTinh,
                   String avatar, Integer trangThai,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;

        // Xác định role dựa trên VaiTroId
        if (vaiTroId == 1) {
            this.role = "admin";
        } else {
            this.role = "employee";
        }

        this.roleId = String.valueOf(vaiTroId);
        this.roleName = vaiTroName;
        this.status = trangThai == 1 ? "active" : "inactive";
        this.address = diaChi;
        this.notes = chucVu;
        this.dateOfBirth = ngaySinh;
        this.gender = gioiTinh;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor cho Khách Hàng
    public UserDTO(Integer id, String name, String email, String phone,
                   String diaChi, LocalDate ngaySinh, String gioiTinh,
                   String avatar, Integer trangThai,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = "customer";
        this.roleId = null;
        this.roleName = "Khách Hàng";
        this.status = trangThai == 1 ? "active" : "inactive";
        this.address = diaChi != null ? diaChi : "";
        this.notes = "";
        this.dateOfBirth = ngaySinh;
        this.gender = gioiTinh;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}