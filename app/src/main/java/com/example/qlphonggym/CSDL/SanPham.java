package com.example.qlphonggym.CSDL;

public class SanPham {

    private String idSanPham;
    private String tenSanPham;
    private String giaSanPham;
    private String moTaSanPham;
    private String imageUrl;
    private String danhMucId;
    private int soLuongNhap;
    private int soLuongConLai;
    private int motSao;
    private int haiSao;
    private int baSao;
    private int bonSao;
    private int namSao;
    private double diemTrungBinh;

    public SanPham() {
    }

    public SanPham(String idSanPham, String tenSanPham, String giaSanPham, String moTaSanPham,
                   String imageUrl, String danhMucId, int soLuongNhap, int soLuongConLai,
                   int motSao, int haiSao, int baSao, int bonSao, int namSao, double diemTrungBinh) {
        this.idSanPham = idSanPham;
        this.tenSanPham = tenSanPham;
        this.giaSanPham = giaSanPham;
        this.moTaSanPham = moTaSanPham;
        this.imageUrl = imageUrl;
        this.danhMucId = danhMucId;
        this.soLuongNhap = soLuongNhap;
        this.soLuongConLai = soLuongConLai;
        this.motSao = motSao;
        this.haiSao = haiSao;
        this.baSao = baSao;
        this.bonSao = bonSao;
        this.namSao = namSao;
        this.diemTrungBinh = diemTrungBinh;
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

    // Getter và Setter cho thuộc tính soLuongNhap
    public int getSoLuongNhap() {
        return soLuongNhap;
    }

    public void setSoLuongNhap(int soLuongNhap) {
        this.soLuongNhap = soLuongNhap;
    }

    // Getter và Setter cho thuộc tính soLuongConLai
    public int getSoLuongConLai() {
        return soLuongConLai;
    }

    public void setSoLuongConLai(int soLuongConLai) {
        this.soLuongConLai = soLuongConLai;
    }

    // Getter và Setter cho các thuộc tính đánh giá sao
    public int getMotSao() {
        return motSao;
    }

    public void setMotSao(int motSao) {
        this.motSao = motSao;
    }

    public int getHaiSao() {
        return haiSao;
    }

    public void setHaiSao(int haiSao) {
        this.haiSao = haiSao;
    }

    public int getBaSao() {
        return baSao;
    }

    public void setBaSao(int baSao) {
        this.baSao = baSao;
    }

    public int getBonSao() {
        return bonSao;
    }

    public void setBonSao(int bonSao) {
        this.bonSao = bonSao;
    }

    public int getNamSao() {
        return namSao;
    }

    public void setNamSao(int namSao) {
        this.namSao = namSao;
    }

    // Getter và Setter cho thuộc tính diemTrungBinh
    public double getDiemTrungBinh() {
        return diemTrungBinh;
    }

    public void setDiemTrungBinh(double diemTrungBinh) {
        this.diemTrungBinh = diemTrungBinh;
    }


    public void tinhDiemTrungBinh() {
        int tongSoDanhGia = motSao + haiSao + baSao + bonSao + namSao;
        int tongDiem = motSao * 1 + haiSao * 2 + baSao * 3 + bonSao * 4 + namSao * 5;

        if (tongSoDanhGia > 0) {
            diemTrungBinh = (double) tongDiem / tongSoDanhGia;
        } else {
            diemTrungBinh = 0.0;
        }
    }

    @Override
    public String toString() {
        return "SanPham{" +
                "idSanPham='" + idSanPham + '\'' +
                ", tenSanPham='" + tenSanPham + '\'' +
                ", giaSanPham='" + giaSanPham + '\'' +
                ", moTaSanPham='" + moTaSanPham + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", danhMucId='" + danhMucId + '\'' +
                ", soLuongNhap=" + soLuongNhap +
                ", soLuongConLai=" + soLuongConLai +
                ", diemTrungBinh=" + diemTrungBinh +
                '}';
    }

    public int getGiaSanPhamInt() {
        try {
            return Integer.parseInt(giaSanPham.replaceAll("[^0-9]", ""));  // Chỉ giữ lại số, loại bỏ mọi ký tự không phải là số
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    public int getSoLuongBanDuoc() {
        return soLuongNhap - soLuongConLai;
    }

}
