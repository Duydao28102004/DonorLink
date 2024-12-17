package com.example.donorlink.blooddonationsitemanagerfragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.donorlink.AddDonationSiteActivity;
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
    private boolean isOwnSitesSelected = false;
    private boolean isVolunteerSitesSelected = false;

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

        firestoreRepository.fetchDonationSites().observe(this, donationSites -> {
            if (donationSites != null) {
                donationSiteLiveData.setValue(donationSites);
            }
        });

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
        plusButtonFrameLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddDonationSiteActivity.class);
            startActivity(intent);
        });


        // Initialize Buttons
        Button buttonOwnSites = view.findViewById(R.id.buttonOwnSites);
        Button buttonVolunteerSites = view.findViewById(R.id.buttonVolunteerSites);

        // First the buttonOwn is selected
        isOwnSitesSelected = true;
        isVolunteerSitesSelected = false;

        // Initially set the button styles based on the selection
        updateButtonStyles(buttonOwnSites, buttonVolunteerSites);

        // Button click listeners to filter the data
        buttonOwnSites.setOnClickListener(v -> {
            isOwnSitesSelected = true;
            isVolunteerSitesSelected = false;
            filterData();
            updateButtonStyles(buttonOwnSites, buttonVolunteerSites); // Update button styles
        });

        buttonVolunteerSites.setOnClickListener(v -> {
            isOwnSitesSelected = false;
            isVolunteerSitesSelected = true;
            filterData();
            updateButtonStyles(buttonOwnSites, buttonVolunteerSites); // Update button styles
        });

        bloodDonationSiteManagerLiveData.observe(getViewLifecycleOwner(), manager -> {
            if (manager != null) {
                TextView donationManagerNameHeader = view.findViewById(R.id.donationManagerNameHeader);
                donationManagerNameHeader.setText(manager.getUsername() + "!");

                // Observe donation sites
                donationSiteLiveData.observe(getViewLifecycleOwner(), donationSites -> {
                    allDonationSites = donationSites;
                    filterData();
                });
            }
        });

        return view;
    }

    private void updateButtonStyles(Button buttonOwnSites, Button buttonVolunteerSites) {
        // Reset both buttons
        buttonOwnSites.setBackgroundColor(getResources().getColor(R.color.border)); // Use your default color
        buttonVolunteerSites.setBackgroundColor(getResources().getColor(R.color.border)); // Use your default color
        buttonOwnSites.setTextColor(getResources().getColor(R.color.black)); // Default text color
        buttonVolunteerSites.setTextColor(getResources().getColor(R.color.black)); // Default text color

        // Update the selected button
        if (isOwnSitesSelected) {
            buttonOwnSites.setBackgroundColor(getResources().getColor(R.color.accent)); // Color for selected button
            buttonOwnSites.setTextColor(getResources().getColor(R.color.white)); // Text color for selected button
        } else if (isVolunteerSitesSelected) {
            buttonVolunteerSites.setBackgroundColor(getResources().getColor(R.color.accent)); // Color for selected button
            buttonVolunteerSites.setTextColor(getResources().getColor(R.color.white)); // Text color for selected button
        }
    }

    private void filterData() {
        // This method is responsible for filtering data based on the selected filter

        BloodDonationSiteManager manager = bloodDonationSiteManagerLiveData.getValue();
        List<DonationSite> donationSites = donationSiteLiveData.getValue();

        if (manager != null && donationSites != null) {
            List<DonationSite> filteredSites = new ArrayList<>();

            if (isOwnSitesSelected) {
                // Filter own sites
                for (DonationSite site : donationSites) {
                    for (DonationSite managerSite : manager.getDonationSites()) {
                        if (managerSite.getName().equals(site.getName())) {
                            filteredSites.add(site);
                            break;
                        }
                    }
                }
            } else if (isVolunteerSitesSelected) {
                // Filter volunteer sites
                for (DonationSite site : donationSites) {
                    for (BloodDonationSiteManager volunteer : site.getVolunteers()) {
                        if (volunteer.getEmail().equals(email)) {
                            filteredSites.add(site);
                            break;
                        }
                    }
                }
            }

            // Update the adapter with filtered data
            adapter.updateData(filteredSites);
        }
    }
}