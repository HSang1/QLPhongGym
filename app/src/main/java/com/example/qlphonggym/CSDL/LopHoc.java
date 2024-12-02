package com.example.qlphonggym.CSDL;

public class LopHoc {

    private String id;
    private String tenLopHoc;

    public LopHoc() {
        // Constructor mặc định cho Firebase
    }

    public LopHoc(String id, String tenLopHoc) {
        this.id = id;
        this.tenLopHoc = tenLopHoc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenLopHoc() {
        return tenLopHoc;
    }

    public void setTenLopHoc(String tenLopHoc) {
        this.tenLopHoc = tenLopHoc;
    }
}
