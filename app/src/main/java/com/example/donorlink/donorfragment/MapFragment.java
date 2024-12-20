package com.example.donorlink.donorfragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorlink.FirestoreRepository;
import com.example.donorlink.R;
import com.example.donorlink.model.DonationSite;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FirestoreRepository firestoreRepository;
    private List<DonationSite> allDonationSites = new ArrayList<>();
    private SearchResultsAdapter adapter;
    private RecyclerView searchResultsRecyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        firestoreRepository = new FirestoreRepository(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Set up the RecyclerView
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchResultsAdapter(new ArrayList<>(), this::onDonationSiteSelected);
        searchResultsRecyclerView.setAdapter(adapter);

        // Set up the SearchView
        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Trigger search when the user presses "Enter"
                searchDonationSites(query);
                return true;  // Returning true means the search action is handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Hide the RecyclerView if search query is cleared
                    searchResultsRecyclerView.setVisibility(View.GONE);
                }
                return false;
            }
        });

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();

        // Fetch and observe donation site data to add markers
        firestoreRepository.fetchDonationSites().observe(getViewLifecycleOwner(), donationSites -> {
            if (donationSites != null) {
                allDonationSites = donationSites;
                displayDonationSiteMarkers(donationSites);
            }
        });
    }

    private void searchDonationSites(String query) {
        if (allDonationSites == null || allDonationSites.isEmpty()) {
            return;
        }

        List<DonationSite> filteredSites = new ArrayList<>();
        for (DonationSite site : allDonationSites) {
            if (site.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredSites.add(site);
            }
        }

        // Show the RecyclerView and update with filtered data
        if (!filteredSites.isEmpty()) {
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
            adapter.updateData(filteredSites);
        } else {
            searchResultsRecyclerView.setVisibility(View.GONE);
            Toast.makeText(getContext(), "No donation sites found", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayDonationSiteMarkers(List<DonationSite> donationSites) {
        if (donationSites.isEmpty()) {
            Toast.makeText(getContext(), "No donation sites available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove any existing markers
        mMap.clear();

        for (DonationSite site : donationSites) {
            LatLng position = new LatLng(site.getLatitude(), site.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(site.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    private void onDonationSiteSelected(DonationSite site) {
        // When a donation site is selected, move the map to the selected location
        LatLng position = new LatLng(site.getLatitude(), site.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        // Hide the RecyclerView after selection
        searchResultsRecyclerView.setVisibility(View.GONE);
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }
}