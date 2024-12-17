package com.example.donorlink;

import com.example.donorlink.model.BloodDonationSiteManager;
import com.example.donorlink.model.DonationSite;
import com.example.donorlink.model.Donor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class sampleData {

    public static List<DonationSite> generateTestDonationSite() {
        List<DonationSite> donationSites = new ArrayList<>();
        // Test data for donors
        List<Donor> donors = new ArrayList<>();
        donors.add(new Donor("John Doe", "john.doe@example.com", "A+"));
        donors.add(new Donor("Jane Smith", "jane.smith@example.com", "A-"));

        // Test data for volunteers (BloodDonationSiteManager)
        List<BloodDonationSiteManager> volunteers = new ArrayList<>();
        volunteers.add(new BloodDonationSiteManager("Alice Manager", "alice.manager@example.com", new ArrayList<>()));
        volunteers.add(new BloodDonationSiteManager("Bob Manager", "bob.manager@example.com", new ArrayList<>()));

        // Blood types available at the site
        List<String> bloodTypes = Arrays.asList("A+", "O+", "B-", "AB-");

        // Create a DonationSite instance
        DonationSite testSite = new DonationSite(
                "Downtown Blood Donation Center",    // Name
                "123 Main Street, City, Country",    // Address
                "9:00 AM - 5:00 PM",                 // Donation Hours
                "https://example.com/donation_site_image.jpg",  // Donor Site Image URL
                "A community-driven blood donation site located in the heart of the city.",  // Description
                101.345678,                          // Longitude
                13.456789                            // Latitude
        );

        // Add sample donation sites to the list
        donationSites.add(testSite);

        // Assign the donors, volunteers, and blood types
        testSite.setDonors(donors);
        testSite.setVolunteers(volunteers);
        testSite.setBloodType(bloodTypes);

        return donationSites;
    }

    public static void printTestDonationSite(DonationSite donationSite) {
        System.out.println("=== Donation Site Test Data ===");
        System.out.println("Name: " + donationSite.getName());
        System.out.println("Address: " + donationSite.getAddress());
        System.out.println("Operating Hours: " + donationSite.getDonationHours());
        System.out.println("Description: " + donationSite.getDescription());
        System.out.println("Latitude: " + donationSite.getLatitude());
        System.out.println("Longitude: " + donationSite.getLongitude());
        System.out.println("Image URL: " + donationSite.getDonorSiteImage());

        System.out.println("\nAvailable Blood Types:");
        for (String bloodType : donationSite.getBloodType()) {
            System.out.println("- " + bloodType);
        }

        System.out.println("\nDonors:");
        for (Donor donor : donationSite.getDonors()) {
            System.out.println("- " + donor.getUsername() + " (" + donor.getBloodType() + ")");
        }

        System.out.println("\nVolunteers:");
        for (BloodDonationSiteManager volunteer : donationSite.getVolunteers()) {
            System.out.println("- " + volunteer.getUsername());
        }
    }
}