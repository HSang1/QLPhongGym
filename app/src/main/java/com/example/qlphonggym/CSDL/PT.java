package com.example.qlphonggym.CSDL;

public class PT {
    private String id;
    private String tenPT;

    public PT() {
        // Constructor mặc định cho Firebase
    }

    public PT(String id, String tenPT) {
        this.id = id;
        this.tenPT = tenPT;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenPT() {
        return tenPT;
    }

    public void setTenPT(String tenLopHoc) {
        this.tenPT = tenPT;
    }
}
