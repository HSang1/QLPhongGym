package com.example.qlphonggym.CSDL;

public class CSDL_Users {
    private String username;
    private String phoneNumber;
    private String email;
    private String city;
    private String fullName;
    private String role;

    public CSDL_Users() {
    }

    public CSDL_Users(String username, String phoneNumber, String email,
                      String city, String fullName, String role) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.city = city;
        this.fullName = fullName;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
