package com.example.donorlink;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorlink.model.DonationSite;

import java.util.ArrayList;
import java.util.List;

public class SuperUserActivity extends AppCompatActivity {

    private FirestoreRepository firestoreRepository;
    private MutableLiveData<List<DonationSite>> donationSiteLiveData = new MutableLiveData<>();
    private DonationSiteAdapter adapter;
    private List<DonationSite> allSites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user);

        getSupportActionBar().hide();

        // Initialize Firestore Repository
        firestoreRepository = new FirestoreRepository(this);

        // Fetch donation sites and observe changes
        firestoreRepository.fetchDonationSites().observe(this, donationSites -> {
            if (donationSites != null) {
                allSites = donationSites;
                donationSiteLiveData.setValue(donationSites);
                adapter.updateData(donationSites);
            }
        });

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DonationSiteAdapter(allSites, this, null); // Adapter without click listeners
        recyclerView.setAdapter(adapter);

        // Set up SearchView for filtering
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterSites(query);
                if (adapter.getItemCount() == 0) {
                    Toast.makeText(SuperUserActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSites(newText);
                return true;
            }
        });
    }

    private void filterSites(String query) {
        if (query == null || query.isEmpty()) {
            adapter.updateData(allSites);
        } else {
            List<DonationSite> filteredSites = new ArrayList<>();
            for (DonationSite site : allSites) {
                if (site.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredSites.add(site);
                }
            }
            adapter.updateData(filteredSites);
        }
    }
}
