package com.example.donorlink.blooddonationsitemanagerfragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private String email;
    private FirestoreRepository firestoreRepository;
    private MutableLiveData<BloodDonationSiteManager> bloodDonationSiteManagerLiveData = new MutableLiveData<>();
    private MutableLiveData<List<DonationSite>> donationSiteLiveData = new MutableLiveData<>();
    private List<DonationSite> allDonationSites = new ArrayList<>();
    private DonationSiteAdapter adapter;
    private boolean isOwnSitesSelected = true;
    private boolean isVolunteerSitesSelected = false;

    // ActivityResultLauncher to handle the result
    private final ActivityResultLauncher<Intent> addSiteLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d("Calling finish adding", "Successfully");

                    // Refresh data by restarting the fragment
                    getActivity().recreate();
                }
            });

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            email = getArguments().getString("email");
        }

        firestoreRepository = new FirestoreRepository(getContext());

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
        adapter = new DonationSiteAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        // Plus button to add a new donation site
        FrameLayout plusButtonFrameLayout = view.findViewById(R.id.buttonFrameLayout);
        plusButtonFrameLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddDonationSiteActivity.class);
            addSiteLauncher.launch(intent); // Launch the AddDonationSiteActivity for a result
        });

        // Initialize Buttons
        Button buttonOwnSites = view.findViewById(R.id.buttonOwnSites);
        Button buttonVolunteerSites = view.findViewById(R.id.buttonVolunteerSites);

        // Set default selection
        isOwnSitesSelected = true;
        isVolunteerSitesSelected = false;
        updateButtonStyles(buttonOwnSites, buttonVolunteerSites);

        // Button click listeners to filter the data
        buttonOwnSites.setOnClickListener(v -> {
            isOwnSitesSelected = true;
            isVolunteerSitesSelected = false;
            filterData();
            updateButtonStyles(buttonOwnSites, buttonVolunteerSites);
        });

        buttonVolunteerSites.setOnClickListener(v -> {
            isOwnSitesSelected = false;
            isVolunteerSitesSelected = true;
            filterData();
            updateButtonStyles(buttonOwnSites, buttonVolunteerSites);
        });

        bloodDonationSiteManagerLiveData.observe(getViewLifecycleOwner(), manager -> {
            if (manager != null) {
                TextView donationManagerNameHeader = view.findViewById(R.id.donationManagerNameHeader);
                donationManagerNameHeader.setText(manager.getUsername() + "!");

                donationSiteLiveData.observe(getViewLifecycleOwner(), donationSites -> {
                    allDonationSites = donationSites;
                    filterData();
                });
            }
        });

        donationSiteLiveData.observe(getViewLifecycleOwner(), donationSites -> {
            if (donationSites != null) {
                allDonationSites = donationSites;
                filterData(); // Refresh the data display
            }
        });

        return view;
    }

    private void updateButtonStyles(Button buttonOwnSites, Button buttonVolunteerSites) {
        buttonOwnSites.setBackgroundColor(getResources().getColor(R.color.border));
        buttonVolunteerSites.setBackgroundColor(getResources().getColor(R.color.border));
        buttonOwnSites.setTextColor(getResources().getColor(R.color.black));
        buttonVolunteerSites.setTextColor(getResources().getColor(R.color.black));

        if (isOwnSitesSelected) {
            buttonOwnSites.setBackgroundColor(getResources().getColor(R.color.accent));
            buttonOwnSites.setTextColor(getResources().getColor(R.color.white));
        } else {
            buttonVolunteerSites.setBackgroundColor(getResources().getColor(R.color.accent));
            buttonVolunteerSites.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void filterData() {
        BloodDonationSiteManager manager = bloodDonationSiteManagerLiveData.getValue();
        List<DonationSite> donationSites = donationSiteLiveData.getValue();
        TextView noDonationSitesText = getView().findViewById(R.id.noDonationSitesText);

        if (manager != null && donationSites != null) {
            List<DonationSite> filteredSites = new ArrayList<>();

            if (isOwnSitesSelected) {
                for (DonationSite site : donationSites) {
                    if (site.getManager().getEmail().equals(email)) {
                        filteredSites.add(site);
                    }
                }
            } else {
                for (DonationSite site : donationSites) {
                    for (BloodDonationSiteManager volunteer : site.getVolunteers()) {
                        if (volunteer.getEmail().equals(email)) {
                            filteredSites.add(site);
                            break;
                        }
                    }
                }
            }

            adapter.updateData(filteredSites);

            // Toggle visibility of the "No donation sites" message
            if (filteredSites.isEmpty()) {
                noDonationSitesText.setVisibility(View.VISIBLE);
            } else {
                noDonationSitesText.setVisibility(View.GONE);
            }
        }
    }

}