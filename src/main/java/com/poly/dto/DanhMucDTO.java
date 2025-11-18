package com.poly.dto;

public class DanhMucDTO {
    private Integer danhMucId;
    private String ten;
    private String moTa;
    private Integer trangThai;

    // Constructors
    public DanhMucDTO() {
    }

    public DanhMucDTO(Integer danhMucId, String ten, String moTa, Integer trangThai) {
        this.danhMucId = danhMucId;
        this.ten = ten;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public Integer getDanhMucId() {
        return danhMucId;
    }

    public void setDanhMucId(Integer danhMucId) {
        this.danhMucId = danhMucId;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Integer getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Integer trangThai) {
        this.trangThai = trangThai;
    }
}