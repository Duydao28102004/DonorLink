package com.example.donorlink.model;

import java.util.List;

public class DonationSite {
    private String name;
    private String address;
    private String donationHours;
    private List<String> bloodType;
    private List<Donor> donors;
    private List<BloodDonationSiteManager> volunteers;

    public DonationSite() {}  // Firestore requires a no-argument constructor

    public DonationSite(String name, String address, String donationHours, List<String> bloodType, List<Donor> donors, List<BloodDonationSiteManager> volunteers) {
        this.name = name;
        this.address = address;
        this.donationHours = donationHours;
        this.bloodType = bloodType;
        this.donors = donors;
        this.volunteers = volunteers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDonationHours() {
        return donationHours;
    }

    public void setDonationHours(String donationHours) {
        this.donationHours = donationHours;
    }

    public List<String> getBloodType() {
        return bloodType;
    }

    public void setBloodType(List<String> bloodType) {
        this.bloodType = bloodType;
    }

    public List<Donor> getDonors() {
        return donors;
    }

    public void setDonors(List<Donor> donors) {
        this.donors = donors;
    }

    public List<BloodDonationSiteManager> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<BloodDonationSiteManager> volunteers) {
        this.volunteers = volunteers;
    }

    public void addDonor(Donor donor) {
        this.donors.add(donor);
    }
}
