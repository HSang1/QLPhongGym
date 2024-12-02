package com.example.qlphonggym.CSDL;

public class GioHang {
    String idSanPham;
    String tenSP;
    String soLuong;
    String giaSP;
    String imageUrl;

    String username;
    String idGioHang;


    public GioHang() {
    }

    public GioHang(String idSanPham, String tenSP, String soLuong, String giaSP, String imageUrl, String username, String idGioHang) {
        this.idSanPham = idSanPham;
        this.tenSP = tenSP;
        this.soLuong = soLuong;
        this.giaSP = giaSP;
        this.imageUrl = imageUrl;
        this.username = username;
        this.idGioHang = idGioHang;
    }

    public String getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(String idSanPham) {
        this.idSanPham = idSanPham;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public String getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(String soLuong) {
        this.soLuong = soLuong;
    }

    public String getGiaSP() {
        return giaSP;
    }

    public void setGiaSP(String giaSP) {
        this.giaSP = giaSP;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdGioHang() {
        return idGioHang;
    }

    public void setIdGioHang(String idGioHang) {
        this.idGioHang = idGioHang;
    }
}
