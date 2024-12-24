package com.example.donorlink.blooddonationsitemanagerfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorlink.DonationSiteAdapter;
import com.example.donorlink.FirestoreRepository;
import com.example.donorlink.R;
import com.example.donorlink.model.DonationSite;

import java.util.List;

public class ExploreFragment extends Fragment {

    private FirestoreRepository firestoreRepository;
    private MutableLiveData<List<DonationSite>> donationSiteLiveData = new MutableLiveData<>();

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestoreRepository = new FirestoreRepository(getContext());

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
        View view = inflater.inflate(R.layout.blood_donation_site_manager_explore_fragment, container, false);

        // Get references to the views
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDonationSites);
        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.searchView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Observe donation sites live data and set the adapter
        donationSiteLiveData.observe(getViewLifecycleOwner(), donationSites -> {
            if (donationSites != null) {
                DonationSiteAdapter donationSiteAdapter = new DonationSiteAdapter(donationSites, getContext(), null);
                recyclerView.setAdapter(donationSiteAdapter);

                // Set up the search functionality
                searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        // Filter the adapter's list
                        donationSiteAdapter.filter(query);

                        // Handle no results found
                        if (donationSiteAdapter.getItemCount() == 0) {
                            Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // Dynamically filter the adapter's list as the user types
                        donationSiteAdapter.filter(newText);
                        return true;
                    }
                });
            }
        });

        return view;
    }
}
