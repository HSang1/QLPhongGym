package com.example.qlphonggym.CSDL;

public class SanPham {

    private String idSanPham;  // Thêm idSanPham
    private String tenSanPham;
    private String giaSanPham;
    private String moTaSanPham;
    private String imageUrl;
    private String danhMucId;

    // Constructor rỗng cho Firebase
    public SanPham() {
    }

    // Constructor đầy đủ để khởi tạo đối tượng
    public SanPham(String idSanPham, String tenSanPham, String giaSanPham, String moTaSanPham, String imageUrl, String danhMucId) {
        this.idSanPham = idSanPham; // Gán idSanPham
        this.tenSanPham = tenSanPham;
        this.giaSanPham = giaSanPham;
        this.moTaSanPham = moTaSanPham;
        this.imageUrl = imageUrl;
        this.danhMucId = danhMucId;
    }

    // Getter và Setter cho thuộc tính idSanPham
    public String getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(String idSanPham) {
        this.idSanPham = idSanPham;
    }

    // Getter và Setter cho thuộc tính tenSanPham
    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    // Getter và Setter cho thuộc tính giaSanPham
    public String getGiaSanPham() {
        return giaSanPham;
    }

    public void setGiaSanPham(String giaSanPham) {
        this.giaSanPham = giaSanPham;
    }

    // Getter và Setter cho thuộc tính moTaSanPham
    public String getMoTaSanPham() {
        return moTaSanPham;
    }

    public void setMoTaSanPham(String moTaSanPham) {
        this.moTaSanPham = moTaSanPham;
    }

    // Getter và Setter cho thuộc tính imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter và Setter cho thuộc tính danhMucId
    public String getDanhMucId() {
        return danhMucId;
    }

    public void setDanhMucId(String danhMucId) {
        this.danhMucId = danhMucId;
    }

    @Override
    public String toString() {
        return "SanPham{" +
                "idSanPham='" + idSanPham + '\'' +  // Cập nhật idSanPham trong phương thức toString
                ", tenSanPham='" + tenSanPham + '\'' +
                ", giaSanPham='" + giaSanPham + '\'' +
                ", moTaSanPham='" + moTaSanPham + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", danhMucId=" + danhMucId +
                '}';
    }
}
