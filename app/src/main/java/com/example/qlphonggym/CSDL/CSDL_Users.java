package com.example.qlphonggym.CSDL;

public class CSDL_Users {
    private String username;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private String fullName; // Thêm trường fullName

    // Constructor
    public CSDL_Users() {
        // Constructor mặc định cần thiết để Firebase sử dụng
    }

    public CSDL_Users(String username, String phoneNumber, String email, String address, String city, String fullName) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.city = city;
        this.fullName = fullName; // Khởi tạo giá trị fullName
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
