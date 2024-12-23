package com.example.donorlink;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.donorlink.model.BloodDonationSiteManager;
import com.example.donorlink.model.DonationSite;
import com.example.donorlink.model.Donor;
import com.example.donorlink.model.User;

public class DonationSiteDetailActivity extends AppCompatActivity {
    FirestoreRepository firestoreRepository;
    AuthenticationRepository authenticationRepository;
    DonationSite donationSite;
    User user;
    Boolean asVolunteer = false;
    Boolean asDonor = false;
    Button signInAsDonorButton;
    Button signInAsVolunteerButton;
    Button editDonationSiteButton;
    Button deleteDonationSiteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_site_detail);
        firestoreRepository = new FirestoreRepository(this);
        authenticationRepository = new AuthenticationRepository();

        // Disable top bar
        getSupportActionBar().hide();

        // Get site name from intent
        String siteName = getIntent().getStringExtra("name");

        // Get content inside the view
        ImageView imageView = findViewById(R.id.imageViewDonationSiteImage);
        TextView siteNameTextView = findViewById(R.id.textViewDonationSiteName);
        TextView siteAddressTextView = findViewById(R.id.textViewDonationSiteAddress);
        TextView siteHoursTextView = findViewById(R.id.textViewDonationSiteHours);
        TextView siteDescriptionTextView = findViewById(R.id.textViewDonationSiteDescription);
        signInAsDonorButton = findViewById(R.id.signInAsDonor);
        signInAsVolunteerButton = findViewById(R.id.signInAsVolunteer);
        editDonationSiteButton = findViewById(R.id.buttonEditDonationSite);
        TextView requiredBloodTypesTextView = findViewById(R.id.textViewRequiredBloodTypes);
        deleteDonationSiteButton = findViewById(R.id.buttonDeleteDonationSite);



        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> onBackPressed());

        // Fetch site details from Firestore and set site details to the views
        firestoreRepository.fetchDonationSites().observe(this, donationSites -> {
            if (donationSites != null) {
                for (DonationSite site : donationSites) {
                    if (site.getName().equals(siteName)) {
                        donationSite = site;
                        imageView.setImageResource(R.drawable.donation_site);
                        siteNameTextView.setText(site.getName());
                        siteAddressTextView.setText("Address: " + site.getAddress());
                        siteHoursTextView.setText("Donation Hours: " + site.getDonationHours());
                        requiredBloodTypesTextView.setText("Required Blood Types: " + String.join(", ", donationSite.getBloodType()));
                        siteDescriptionTextView.setText("Description: " + site.getDescription());

                        // Check user status
                        checkStatus();
                    }
                }
            }
        });

        // Attach click listeners to buttons
        signInAsVolunteerButton.setOnClickListener(v -> signInAsVolunteer());
        signInAsDonorButton.setOnClickListener(v -> signInAsDonor());
    }

    private void checkStatus() {
        // Check if the user is a Blood Donation Site Manager
        firestoreRepository.fetchBloodDonationSiteManagers().observe(this, bloodDonationSiteManagers -> {
            if (bloodDonationSiteManagers != null) {
                for (BloodDonationSiteManager manager : bloodDonationSiteManagers) {
                    if (manager.getEmail().equals(authenticationRepository.getCurrentUser().getEmail())) {
                        user = manager;

                        // Check if the current user is the manager of this site
                        if (donationSite.getManager().getEmail().equals(manager.getEmail())) {
                            // Show "Delete" button
                            findViewById(R.id.buttonDeleteDonationSite).setVisibility(View.VISIBLE);
                            deleteDonationSiteButton.setOnClickListener(v -> showDeleteConfirmationDialog());

                            // Existing code for editing site
                            findViewById(R.id.buttonEditDonationSite).setVisibility(View.VISIBLE);
                            editDonationSiteButton.setOnClickListener(v -> {
                                Intent intent = new Intent(DonationSiteDetailActivity.this, UpdateDonationSiteActivity.class);
                                intent.putExtra("name", donationSite.getName());
                                startActivityForResult(intent, 100);
                            });
                            findViewById(R.id.buttonEditDonationSite).setVisibility(View.VISIBLE);
                            editDonationSiteButton.setOnClickListener(v -> {
                                Intent intent = new Intent(DonationSiteDetailActivity.this, UpdateDonationSiteActivity.class);
                                intent.putExtra("name", donationSite.getName());
                                startActivityForResult(intent, 100);
                            });
                        }

                        // Enable "Sign in as Volunteer" button
                        Button signInAsVolunteerButton = findViewById(R.id.signInAsVolunteer);
                        signInAsVolunteerButton.setVisibility(View.VISIBLE);

                        // Check if the manager is a volunteer at this site
                        for (BloodDonationSiteManager currentManager : donationSite.getVolunteers()) {
                            if (currentManager.getEmail().equals(manager.getEmail())) {
                                signInAsVolunteerButton.setText("Sign Out As Volunteer");
                                asVolunteer = true;
                                break;
                            }
                        }

                        return; // Exit once manager role is identified
                    }
                }
            }
        });

        // Check if the user is a Donor
        firestoreRepository.fetchDonors().observe(this, donors -> {
            if (donors != null) {
                for (Donor donor : donors) {
                    if (donor.getEmail().equals(authenticationRepository.getCurrentUser().getEmail())) {
                        user = donor;

                        // Enable "Sign in as Donor" button
                        Button signInAsDonorButton = findViewById(R.id.signInAsDonor);
                        signInAsDonorButton.setVisibility(View.VISIBLE);

                        // Check if the donor is already signed up for this site
                        for (Donor currentDonor : donationSite.getDonors()) {
                            if (currentDonor.getEmail().equals(donor.getEmail())) {
                                signInAsDonorButton.setText("Sign Out As Donor");
                                asDonor = true;
                                break;
                            }
                        }

                        return; // Exit once donor role is identified
                    }
                }
            }
        });
    }

    private void signInAsVolunteer() {
        if (user instanceof BloodDonationSiteManager && donationSite != null) {
            if (asVolunteer) {
                // Remove the volunteer by email
                donationSite.getVolunteers().removeIf(manager -> manager.getEmail().equals(user.getEmail()));
                firestoreRepository.updateDonationSite(donationSite);
                Button signInAsVolunteerButton = findViewById(R.id.signInAsVolunteer);
                signInAsVolunteerButton.setText("Sign In As Volunteer");
                asVolunteer = false;
            } else {
                // Add the user as a volunteer
                donationSite.getVolunteers().add((BloodDonationSiteManager) user);
                firestoreRepository.updateDonationSite(donationSite);
                Button signInAsVolunteerButton = findViewById(R.id.signInAsVolunteer);
                signInAsVolunteerButton.setText("Sign Out As Volunteer");
                asVolunteer = true;
            }
        }
    }

    private void signInAsDonor() {
        if (user instanceof Donor && donationSite != null) {
            Donor donor = (Donor) user;

            // Check if the donor's blood type matches the required blood types
            if (!donationSite.getBloodType().contains(donor.getBloodType())) {
                // Show a message and prevent sign-in
                Toast.makeText(this, "Your blood type does not match the required types for this site.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (asDonor) {
                // Remove the donor by email
                donationSite.getDonors().removeIf(currentDonor -> currentDonor.getEmail().equals(donor.getEmail()));
                firestoreRepository.updateDonationSite(donationSite);
                Button signInAsDonorButton = findViewById(R.id.signInAsDonor);
                signInAsDonorButton.setText("Sign In As Donor");
                asDonor = false;
            } else {
                // Add the donor
                donationSite.getDonors().add(donor);
                firestoreRepository.updateDonationSite(donationSite);
                Button signInAsDonorButton = findViewById(R.id.signInAsDonor);
                signInAsDonorButton.setText("Sign Out As Donor");
                asDonor = true;
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this donation site?")
                .setPositiveButton("Yes", (dialog, which) -> deleteDonationSite())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteDonationSite() {
        // Remove the donation site from Firestore
        firestoreRepository.deleteDonationSite(donationSite.getName());
        Toast.makeText(DonationSiteDetailActivity.this, "Donation site deleted successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        // Set the result as RESULT_OK
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);

        // Call the default back press behavior
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            recreate();
        }
    }

}