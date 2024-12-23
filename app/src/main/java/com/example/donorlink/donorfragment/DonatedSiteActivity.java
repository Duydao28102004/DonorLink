package com.example.donorlink.donorfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorlink.DonationSiteAdapter;
import com.example.donorlink.R;
import com.example.donorlink.model.DonationSite;
import com.example.donorlink.model.Donor;
import com.example.donorlink.FirestoreRepository;
import java.util.ArrayList;
import java.util.List;

public class DonatedSiteActivity extends AppCompatActivity {

    private static final int ADD_SITE_REQUEST_CODE = 100; // Unique request code
    private String email;
    private FirestoreRepository firestoreRepository;
    private MutableLiveData<List<DonationSite>> donatedSitesLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donated_sites_activity);

        // Disable top action bar
        getSupportActionBar().hide();

        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        // Retrieve the email from the Intent
        if (getIntent() != null) {
            email = getIntent().getStringExtra("email");
        }

        firestoreRepository = new FirestoreRepository(this);

        // Initial data load
        reloadDonationSites();

        // Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewDonatedSites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        donatedSitesLiveData.observe(this, donatedSites -> {
            if (donatedSites != null && donatedSites.size() > 0) {
                // If there are donated sites, show the RecyclerView and hide the message
                recyclerView.setVisibility(View.VISIBLE);
                findViewById(R.id.noDonationMessage).setVisibility(View.GONE);

                // Update the adapter with new data
                DonationSiteAdapter adapter = new DonationSiteAdapter(donatedSites, this);
                recyclerView.setAdapter(adapter);
            } else {
                // If no donated sites, hide the RecyclerView and show the "No donation yet" message
                recyclerView.setVisibility(View.GONE);
                findViewById(R.id.noDonationMessage).setVisibility(View.VISIBLE);
            }
        });
    }

    private void reloadDonationSites() {
        firestoreRepository.fetchDonationSites().observe(this, donationSites -> {
            if (donationSites != null) {
                List<DonationSite> donatedSites = new ArrayList<>();
                for (DonationSite site : donationSites) {
                    for (Donor donor : site.getDonors()) {
                        if (donor.getEmail().equals(email)) {
                            donatedSites.add(site);
                            break;
                        }
                    }
                }
                donatedSitesLiveData.setValue(donatedSites);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Reload the data from Firestore
            firestoreRepository.fetchDonationSites().observe(this, donationSites -> {
                if (donationSites != null) {
                    List<DonationSite> donatedSites = new ArrayList<>();
                    for (DonationSite site : donationSites) {
                        for (Donor donor : site.getDonors()) {
                            if (donor.getEmail().equals(email)) {
                                donatedSites.add(site);
                                break;
                            }
                        }
                    }
                    donatedSitesLiveData.setValue(donatedSites);
                }
            });
        }
    }
}
