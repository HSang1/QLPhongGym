package com.example.qlphonggym.CSDL;

public class PT {
    private String id;
    private String tenPT;

    // Constructor mặc định (cần thiết cho Firebase)
    public PT() {
    }

    public PT(String id, String tenPT) {
        this.id = id;
        this.tenPT = tenPT;
    }

    // Getter và Setter cho 'id'
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter và Setter cho 'tenPT'
    public String getTenPT() {
        return tenPT;
    }

    public void setTenPT(String tenPT) {
        this.tenPT = tenPT;
    }
}
