package com.example.donorlink.donorfragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donorlink.DonationSiteAdapter;
import com.example.donorlink.FirestoreRepository;
import com.example.donorlink.NotificationActivity;
import com.example.donorlink.R;
import com.example.donorlink.model.DonationSite;
import com.example.donorlink.model.Donor;

import java.util.List;

public class HomeFragment extends Fragment {
    private String email;
    private FirestoreRepository firestoreRepository;
    private MutableLiveData<Donor> donorLiveData = new MutableLiveData<>();
    private MutableLiveData<List<DonationSite>> donationSiteLiveData = new MutableLiveData<>();

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

        // Fetch donors and observe changes
        firestoreRepository.fetchDonors().observe(this, donors -> {
            if (donors != null) {
                for (Donor donor : donors) {
                    if (donor.getEmail().equals(email)) {
                        donorLiveData.setValue(donor);
                        break;
                    }
                }
            }
        });

        // Fetch donation sites list and observe changes
        firestoreRepository.fetchDonationSites().observe(this, donationSites -> {
            if (donationSites != null) {
                donationSiteLiveData.setValue(donationSites);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.donor_home_fragment, container, false);

        // Get references to the views
        TextView donorNameHeader = view.findViewById(R.id.donorNameHeader);
        TextView donorName = view.findViewById(R.id.tvDonorName);
        TextView donationSite = view.findViewById(R.id.tvDonationSites);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDonationSites);
        TextView seeAllText = view.findViewById(R.id.tvSeeAllDonationSites);
        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.searchView);
        TextView noDonationSitesText = view.findViewById(R.id.noDonationSitesText);
        FrameLayout notificationButton = view.findViewById(R.id.notificationButton);

        // open notification activity
        notificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            startActivity(intent);
        });

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Observe the donorLiveData and update the UI when the donor is set
        donorLiveData.observe(getViewLifecycleOwner(), donor -> {
            if (donor != null) {
                donorNameHeader.setText(donor.getUsername() + "!");
                donorName.setText("Let's start, " + donor.getUsername());
                seeAllText.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("email", donor.getEmail());
                    Intent intent = new Intent(getContext(), DonatedSiteActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                });

                // Observe donation sites once donor data is available
                donationSiteLiveData.observe(getViewLifecycleOwner(), donationSites -> {
                    if (donationSites != null) {
                        int counter = 0;

                        // Set up the adapter
                        DonationSiteAdapter donationSiteAdapter = new DonationSiteAdapter(donationSites, getContext(), noDonationSitesText);
                        recyclerView.setAdapter(donationSiteAdapter);

                        for (DonationSite site : donationSites) {
                            for (Donor currentDonor : site.getDonors()) {
                                if (currentDonor.getEmail().equals(donor.getEmail())) {
                                    counter++;
                                }
                            }
                        }

                        if (counter == 0) {
                            donationSite.setText("You have donated to 0 sites");
                        } else {
                            donorName.setText("Welcome, " + donor.getUsername());
                            donationSite.setText("You have donated to " + counter + " sites");
                        }

                        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                // Filter the adapter's list
                                donationSiteAdapter.filter(query);

                                // Close the keyboard and clear focus
                                searchView.clearFocus();

                                // Handle visibility of the RecyclerView and the "No results found" message
                                if (donationSiteAdapter.getItemCount() == 0) {
                                    recyclerView.setVisibility(View.GONE); // Hide RecyclerView if no results
                                    noDonationSitesText.setVisibility(View.VISIBLE); // Show "No results found" message
                                } else {
                                    recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView if there are results
                                    noDonationSitesText.setVisibility(View.GONE); // Hide "No results found" message
                                }
                                return true; // Indicate the query has been handled
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                // Filter the adapter's list as user types
                                donationSiteAdapter.filter(newText);

                                // Handle visibility of the RecyclerView and the "No results found" message
                                if (donationSiteAdapter.getItemCount() == 0) {
                                    recyclerView.setVisibility(View.GONE); // Hide RecyclerView if no results
                                    noDonationSitesText.setVisibility(View.VISIBLE); // Show "No results found" message
                                } else {
                                    recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView if there are results
                                    noDonationSitesText.setVisibility(View.GONE); // Hide "No results found" message
                                }
                                return false; // Return false so the query change isn't handled again by `onQueryTextSubmit`
                            }
                        });

                    }
                });
            }
        });

        return view;
    }

}