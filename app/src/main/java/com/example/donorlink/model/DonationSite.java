package com.example.donorlink.model;

import java.util.ArrayList;
import java.util.List;

public class DonationSite {
    private String name;
    private String address;
    private String donationHours;
    private String donorSiteImage;
    private String description;
    private double latitude;
    private double longitude;
    private BloodDonationSiteManager manager;
    private List<String> bloodType;
    private List<Donor> donors;
    private List<BloodDonationSiteManager> volunteers;

    public DonationSite() {}  // Firestore requires a no-argument constructor

    public DonationSite(String name, String address, String donationHours, String donorSiteImage, String description, double longitude, double latitude, BloodDonationSiteManager manager) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.donorSiteImage = donorSiteImage;
        this.donationHours = donationHours;
        this.address = address;
        this.name = name;
        this.manager = manager;
        this.bloodType = new ArrayList<>();
        this.donors = new ArrayList<>();
        this.volunteers = new ArrayList<>();
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

    public List<BloodDonationSiteManager> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<BloodDonationSiteManager> volunteers) {
        this.volunteers = volunteers;
    }

    public List<Donor> getDonors() {
        return donors;
    }

    public void setDonors(List<Donor> donors) {
        this.donors = donors;
    }

    public List<String> getBloodType() {
        return bloodType;
    }

    public void setBloodType(List<String> bloodType) {
        this.bloodType = bloodType;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDonorSiteImage() {
        return donorSiteImage;
    }

    public void setDonorSiteImage(String donorSiteImage) {
        this.donorSiteImage = donorSiteImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDonationHours() {
        return donationHours;
    }

    public void setDonationHours(String donationHours) {
        this.donationHours = donationHours;
    }

    public BloodDonationSiteManager getManager() {
        return manager;
    }

    public void setManager(BloodDonationSiteManager manager) {
        this.manager = manager;
    }
}
