package com.example.donorlink.blooddonationsitemanagerfragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.donorlink.DonationSiteAdapter;
import com.example.donorlink.FirestoreRepository;
import com.example.donorlink.R;
import com.example.donorlink.model.BloodDonationSiteManager;
import com.example.donorlink.model.DonationSite;
import com.example.donorlink.sampleData;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private String email;
    private FirestoreRepository firestoreRepository;
    private MutableLiveData<BloodDonationSiteManager> bloodDonationSiteManagerLiveData = new MutableLiveData<>();
    private MutableLiveData<List<DonationSite>> donationSiteLiveData = new MutableLiveData<>();
    private List<DonationSite> allDonationSites = new ArrayList<>();
    private DonationSiteAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            email = getArguments().getString("email");
        }

        firestoreRepository = new FirestoreRepository();

        // Fetch blood donation site managers and observe changes
        firestoreRepository.fetchBloodDonationSiteManagers().observe(this, bloodDonationSiteManagers -> {
            if (bloodDonationSiteManagers != null) {
                for (BloodDonationSiteManager manager : bloodDonationSiteManagers) {
                    if (manager.getEmail().equals(email)) {
                        bloodDonationSiteManagerLiveData.setValue(manager);
                        break;
                    }
                }
            }
        });

        // Load sample data for testing purposes
        donationSiteLiveData.setValue(sampleData.getSampleDonationSites());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.blood_donation_site_manager_home_fragment, container, false);

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDonationSites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DonationSiteAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Get the plus button frame layout
        FrameLayout plusButtonFrameLayout = view.findViewById(R.id.buttonFrameLayout);


        // Initialize Buttons
        Button buttonOwnSites = view.findViewById(R.id.buttonOwnSites);
        Button buttonVolunteerSites = view.findViewById(R.id.buttonVolunteerSites);

        bloodDonationSiteManagerLiveData.observe(getViewLifecycleOwner(), manager -> {
            if (manager != null) {
                TextView donationManagerNameHeader = view.findViewById(R.id.donationManagerNameHeader);
                donationManagerNameHeader.setText(manager.getUsername() + "!");

                // Observe donation sites
                donationSiteLiveData.observe(getViewLifecycleOwner(), donationSites -> {
                    allDonationSites = donationSites;
                    adapter.updateData(allDonationSites); // Initially display all sites
                });

                // Button click listeners to filter the data
                buttonOwnSites.setOnClickListener(v -> filterOwnSites());
                buttonVolunteerSites.setOnClickListener(v -> filterVolunteerSites());
            }
        });

        return view;
    }

    private void filterOwnSites() {
        BloodDonationSiteManager manager = bloodDonationSiteManagerLiveData.getValue();
        if (manager != null) {
            List<DonationSite> ownSites = new ArrayList<>();
            for (DonationSite site : allDonationSites) {
                if (manager.getDonationSites().contains(site)) {
                    ownSites.add(site);
                }
            }
            adapter.updateData(ownSites);
        }
    }

    private void filterVolunteerSites() {
        BloodDonationSiteManager manager = bloodDonationSiteManagerLiveData.getValue();
        if (manager != null) {
            List<DonationSite> volunteerSites = new ArrayList<>();
            for (DonationSite site : allDonationSites) {
                for (BloodDonationSiteManager volunteer : site.getVolunteers()) {
                    if (volunteer.getEmail().equals(email)) {
                        volunteerSites.add(site);
                        break;
                    }
                }
            }
            adapter.updateData(volunteerSites);
        }
    }
}