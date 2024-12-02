package com.example.qlphonggym.CSDL;

public class CSDL_NhanXet {
    private String username;
    private String moTa;
    private String ngayGioGopY;  // Thêm trường ngày/giờ góp ý

    // Constructor không tham số
    public CSDL_NhanXet() {
    }

    // Constructor có tham số
    public CSDL_NhanXet(String username, String moTa, String ngayGioGopY) {
        this.username = username;
        this.moTa = moTa;
        this.ngayGioGopY = ngayGioGopY;  // Khởi tạo trường ngày/giờ
    }

    // Getter và Setter cho username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter và Setter cho mô tả
    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    // Getter và Setter cho ngày giờ góp ý
    public String getNgayGioGopY() {
        return ngayGioGopY;
    }

    public void setNgayGioGopY(String ngayGioGopY) {
        this.ngayGioGopY = ngayGioGopY;
    }
}
