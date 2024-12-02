package com.example.qlphonggym.CSDL;

import java.util.List;

public class DatLop {

    private String idDatLop;  // Thêm idDatLop
    private String tenLopHoc;
    private String thoiGianBatDau;
    private String thoiLuong;
    private String thanhPho;
    private String diaDiem;
    private String imageUrl;
    private String lopHocId;
    private List<String> days; // Các ngày xuất hiện của lớp học

    // Constructor rỗng cho Firebase
    public DatLop() {
    }

    // Constructor đầy đủ để khởi tạo đối tượng
    public DatLop(String idDatLop, String tenLopHoc, String thoiGianBatDau, String thoiLuong, String thanhPho, String diaDiem, String imageUrl, String lopHocId, List<String> days) {
        this.idDatLop = idDatLop; // Gán idSanPham
        this.tenLopHoc = tenLopHoc;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiLuong = thoiLuong;
        this.thanhPho = thanhPho;
        this.diaDiem = diaDiem;
        this.imageUrl = imageUrl;
        this.lopHocId =lopHocId;
        this.days = days;
    }

    // Getter và Setter cho thuộc tính idSanPham
    public String getIdDatLop() {
        return idDatLop;
    }

    public void setIdDatLop(String idDatLop) {
        this.idDatLop = idDatLop;
    }

    // Getter và Setter cho thuộc tính tenSanPham
    public String getTenLopHoc() {
        return tenLopHoc;
    }

    public void setTenLopHoc(String tenLopHoc) {
        this.tenLopHoc = tenLopHoc;
    }

    // Getter và Setter cho thuộc tính giaSanPham
    public String getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(String thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    // Getter và Setter cho thuộc tính moTaSanPham
    public String getThoiLuong() {
        return thoiLuong;
    }

    public void setThoiLuong(String thoiLuong) {
        this.thoiLuong = thoiLuong;
    }

    // Getter và Setter cho thuộc tính moTaSanPham
    public String getThanhPho() {
        return thanhPho;
    }

    public void setThanhPho(String thanhPho) {
        this.thanhPho = thanhPho;
    }

    // Getter và Setter cho thuộc tính moTaSanPham
    public String getDiaDiem() {
        return diaDiem;
    }

    public void setDiaDiem(String diaDiem) {
        this.diaDiem = diaDiem;
    }

    // Getter và Setter cho thuộc tính imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter và Setter cho thuộc tính danhMucId
    public String getLopHocId() {
        return lopHocId;
    }

    public void setLopHocId(String lopHocId) {
        this.lopHocId = lopHocId;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    @Override
    public String toString() {
        return "DatLop{" +
                "idDatLop='" + idDatLop + '\'' +  // Cập nhật idSanPham trong phương thức toString
                ", tenLopHoc='" + tenLopHoc + '\'' +
                ", thoiGianBatDau='" + thoiGianBatDau + '\'' +
                ", thoiLuong='" + thoiLuong + '\'' +
                ", thanhPho='" + thanhPho + '\'' +
                ", diaDiem='" + diaDiem + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", lopHocId=" + lopHocId +
                ", days=" + days +
                '}';
    }
}
