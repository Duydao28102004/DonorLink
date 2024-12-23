package com.example.donorlink;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.donorlink.model.DonationSite;

public class DonationSiteDetailActivity extends AppCompatActivity {
    FirestoreRepository firestoreRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_site_detail);
        firestoreRepository = new FirestoreRepository(this);

        // Get site name from intent
        String siteName = getIntent().getStringExtra("name");

        // Get content inside the view
        ImageView imageView = findViewById(R.id.imageViewDonationSiteImage);
        TextView siteNameTextView = findViewById(R.id.textViewDonationSiteName);
        TextView siteAddressTextView = findViewById(R.id.textViewDonationSiteAddress);
        TextView siteHoursTextView = findViewById(R.id.textViewDonationSiteHours);
        TextView siteDescriptionTextView = findViewById(R.id.textViewDonationSiteDescription);

        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        // Fetch site details from Firestore and Set site details to the views
        firestoreRepository.fetchDonationSites().observe(this, donationSites -> {
            if (donationSites != null) {
                for (DonationSite site : donationSites) {
                    if (site.getName().equals(siteName)) {
                        imageView.setImageResource(R.drawable.donation_site); // Set the image
                        siteNameTextView.setText(site.getName());
                        siteAddressTextView.setText("Address: " + site.getAddress());
                        siteHoursTextView.setText("Donation Hours: " + site.getDonationHours());
                        siteDescriptionTextView.setText("Description: " + site.getDescription());
                    }
                }
            }
        });
    }
}