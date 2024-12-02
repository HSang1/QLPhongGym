package com.example.qlphonggym.CSDL;

import java.util.List;

public class DatPT {
    private String idDatPT;  // ID của DatPT
    private String thanhPho;
    private String diaDiem;
    private String imageUrl;
    private String pTId; // ID của PT
    private List<String> days; // Các ngày xuất hiện
    private List<String> sessions; // Các buổi (sáng, trưa, chiều, tối)

    // Constructor rỗng cho Firebase
    public DatPT() {
    }

    // Constructor đầy đủ
    public DatPT(String idDatPT, String thanhPho, String diaDiem, String imageUrl, String pTId, List<String> days, List<String> sessions) {
        this.idDatPT = idDatPT;
        this.thanhPho = thanhPho;
        this.diaDiem = diaDiem;
        this.imageUrl = imageUrl;
        this.pTId = pTId;
        this.days = days;
        this.sessions = sessions;
    }

    // Getter và Setter cho idDatPT
    public String getIdDatPT() {
        return idDatPT;
    }

    public void setIdDatPT(String idDatPT) {
        this.idDatPT = idDatPT;
    }

    // Getter và Setter cho thanhPho
    public String getThanhPho() {
        return thanhPho;
    }

    public void setThanhPho(String thanhPho) {
        this.thanhPho = thanhPho;
    }

    // Getter và Setter cho diaDiem
    public String getDiaDiem() {
        return diaDiem;
    }

    public void setDiaDiem(String diaDiem) {
        this.diaDiem = diaDiem;
    }

    // Getter và Setter cho imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter và Setter cho pTId
    public String getPTId() {
        return pTId;
    }

    public void setPTId(String pTId) {
        this.pTId = pTId;
    }

    // Getter và Setter cho days
    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    // Getter và Setter cho sessions
    public List<String> getSessions() {
        return sessions;
    }

    public void setSessions(List<String> sessions) {
        this.sessions = sessions;
    }

    @Override
    public String toString() {
        return "DatPT{" +
                "idDatPT='" + idDatPT + '\'' +
                ", thanhPho='" + thanhPho + '\'' +
                ", diaDiem='" + diaDiem + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", pTId='" + pTId + '\'' +
                ", days=" + days +
                ", sessions=" + sessions +
                '}';
    }
}