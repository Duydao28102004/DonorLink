package com.example.donorlink.model;

import java.util.List;

public class BloodDonationSiteManager extends User {

    public BloodDonationSiteManager() {}  // Firestore requires a no-argument constructor

    public BloodDonationSiteManager(String username, String email) {
        super(username, email);
    }
}