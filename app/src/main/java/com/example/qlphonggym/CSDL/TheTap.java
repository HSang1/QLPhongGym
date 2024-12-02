package com.example.qlphonggym.CSDL;

public class TheTap {
    private String username;
    private String loaiThe;
    private String ngayBatDau;  // "2024-11-01"
    private String ngayKetThuc;    // "2025-11-01"

    public TheTap(){

    }


    public TheTap(String username, String loaiThe, String ngayBatDau, String ngayKetThuc) {
        this.username = username;
        this.loaiThe = loaiThe;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoaiThe() {
        return loaiThe;
    }

    public void setLoaiThe(String loaiThe) {
        this.loaiThe = loaiThe;
    }

    public String getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(String ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public String getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(String ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }
}
