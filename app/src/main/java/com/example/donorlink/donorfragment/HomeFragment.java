package com.example.donorlink.donorfragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.donorlink.DonationSiteAdapter;
import com.example.donorlink.FirestoreRepository;
import com.example.donorlink.R;
import com.example.donorlink.sampleData;
import com.example.donorlink.model.DonationSite;
import com.example.donorlink.model.Donor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

        firestoreRepository = new FirestoreRepository();

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
//        firestoreRepository.fetchDonationSites().observe(this, donationSites -> {
//            if (donationSites != null) {
//                donationSiteLiveData.setValue(donationSites);
//            }
//        });
        donationSiteLiveData.setValue(sampleData.getSampleDonationSites());
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

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the adapter
        DonationSiteAdapter donationSiteAdapter = new DonationSiteAdapter(donationSiteLiveData.getValue());
        recyclerView.setAdapter(donationSiteAdapter);

        // Observe the donorLiveData and update the UI when the donor is set
        donorLiveData.observe(getViewLifecycleOwner(), donor -> {
            if (donor != null) {
                donorNameHeader.setText(donor.getUsername() + "!");
                donorName.setText("Let's start, " + donor.getUsername());

                // Observe donation sites once donor data is available
                donationSiteLiveData.observe(getViewLifecycleOwner(), donationSites -> {
                    if (donationSites != null) {
                        int counter = 0;

                        for (DonationSite site : donationSites) {
                            if (site.getDonors().contains(donor)) {
                                counter++;
                            }
                        }

                        if (counter == 0) {
                            donationSite.setText("You have donated to 0 sites");
                        } else {
                            donorName.setText("Welcome, " + donor.getUsername());
                            donationSite.setText("You have donated to " + counter + " sites");
                        }
                    }
                });
            }
        });



        return view;
    }
}