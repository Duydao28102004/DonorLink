package com.example.donorlink.model;

import java.util.List;

public class BloodDonationSiteManager {
    private List<DonationSite> donationSites;

    public BloodDonationSiteManager() {}  // Firestore requires a no-argument constructor

    public BloodDonationSiteManager(List<DonationSite> donationSites) {
        this.donationSites = donationSites;
    }

    public List<DonationSite> getDonationSites() {
        return donationSites;
    }

    public void setDonationSites(List<DonationSite> donationSites) {
        this.donationSites = donationSites;
    }
}
