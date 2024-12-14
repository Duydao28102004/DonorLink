package com.example.donorlink;

import com.example.donorlink.model.DonationSite;
import com.example.donorlink.model.Donor;
import com.example.donorlink.model.BloodDonationSiteManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class sampleData {

    public static List<DonationSite> getSampleDonationSites() {
        // Create sample donors
        Donor donor1 = new Donor("donor1", "donor1@example.com", "O+");
        Donor donor2 = new Donor("donor2", "donor2@example.com", "A+");

        // Create sample volunteers
        BloodDonationSiteManager volunteer1 = new BloodDonationSiteManager("Volunteer 1", "volunteer1@example.com", new ArrayList<>());
        BloodDonationSiteManager volunteer2 = new BloodDonationSiteManager("Volunteer 2", "volunteer2@example.com", new ArrayList<>());

        // Create the list of blood types
        List<String> bloodTypes = Arrays.asList("O+", "A+", "B+", "AB+");

        // Create the list of donation sites
        List<DonationSite> donationSites = new ArrayList<>();

        // Add some sample donation sites
        donationSites.add(new DonationSite(
                "Central Blood Bank",
                "123 Blood St, Blood City",
                "9:00 AM - 5:00 PM",
                "central_blood_bank_image_url",
                "The central blood bank for the region.",
                bloodTypes,
                Arrays.asList(donor1, donor2),  // Add donors to this site
                Arrays.asList(volunteer1, volunteer2)  // Add volunteers to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        donationSites.add(new DonationSite(
                "Eastside Blood Drive",
                "456 Donation Ave, East City",
                "10:00 AM - 4:00 PM",
                "eastside_blood_drive_image_url",
                "Mobile blood donation drive in the east side.",
                bloodTypes,
                Arrays.asList(donor2),  // Add donor2 to this site
                Arrays.asList(volunteer2)  // Add volunteer2 to this site
        ));

        return donationSites;
    }
}
