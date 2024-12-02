package com.example.qlphonggym.CSDL;

public class DanhGiaSanPham {
    private String idSanPham;
    private String username;
    private int rating;

    public DanhGiaSanPham() {
        // Constructor mặc định cho Firebase
    }

    public DanhGiaSanPham(String idSanPham, String username, int rating) {
        this.idSanPham = idSanPham;
        this.username = username;
        this.rating = rating;
    }

    // Getter và Setter
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
