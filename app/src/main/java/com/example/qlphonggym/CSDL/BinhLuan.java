package com.example.qlphonggym.CSDL;

public class BinhLuan {
    private String idBinhLuan;
    private String idSanPham;  // ID của sản phẩm
    private String username;    // Tên người dùng
    private String moTa;       // Nội dung bình luận
    private float rating;      // Đánh giá sao

    public BinhLuan() {
        // Constructor mặc định để Firebase có thể sử dụng
    }

    public BinhLuan(String idBinhLuan,String idSanPham, String username, String moTa, float rating) {
        this.idSanPham = idSanPham;
        this.idBinhLuan = idBinhLuan;
        this.username = username;
        this.moTa = moTa;
        this.rating = rating;
    }

    public String getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(String idSanPham) {
        this.idSanPham = idSanPham;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
