package com.example.donorlink.model;

public class Donor extends User {
    private String BloodType;

    public Donor() {}  // Firestore requires a no-argument constructor

    public Donor(String username, String email, String BloodType) {
        super(username, email);
        this.BloodType = BloodType;
    }

    public String getBloodType() {
        return BloodType;
    }

    public void setBloodType(String BloodType) {
        this.BloodType = BloodType;
    }
}
