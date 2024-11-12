package com.example.qlphonggym.CSDL;

public class DanhMuc {

    private String id;
    private String tenDanhMuc;

    public DanhMuc() {
        // Constructor mặc định cho Firebase
    }

    public DanhMuc(String id, String tenDanhMuc) {
        this.id = id;
        this.tenDanhMuc = tenDanhMuc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }
}
