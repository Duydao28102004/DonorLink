package com.example.donorlink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.example.donorlink.model.BloodDonationSiteManager;
import com.example.donorlink.model.DonationSite;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class AddDonationSiteActivity extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private EditText editTextDonationSiteName, editTextDonationHours, editTextDescription;
    private TextView textViewSelectedAddress;
    private Button buttonSearchAddress, buttonSubmit;

    private String selectedAddress;
    private LatLng selectedLocation;

    private FirestoreRepository firestoreRepository;
    private AuthenticationRepository authenticationRepository;
    MutableLiveData<BloodDonationSiteManager> managers = new MutableLiveData<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donation_site);

        // Get the repo instance
        firestoreRepository = new FirestoreRepository();
        authenticationRepository = new AuthenticationRepository();

        // Initialize Views
        editTextDonationSiteName = findViewById(R.id.editTextDonationSiteName);
        editTextDonationHours = findViewById(R.id.editTextDonationHours);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewSelectedAddress = findViewById(R.id.textViewSelectedAddress);
        buttonSearchAddress = findViewById(R.id.buttonSearchAddress);
        buttonSubmit = findViewById(R.id.buttonSubmitDonationSite);

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAmYG0ewlmb4zaJAkC6pBsFjqi0NBQu-Po");
        }

        // Button click to open Autocomplete for address selection
        buttonSearchAddress.setOnClickListener(v -> openAutocomplete());

        // Submit the form
        buttonSubmit.setOnClickListener(v -> submitDonationSite());
    }

    // Method to open Google Places Autocomplete
    private void openAutocomplete() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    // Handle the result of the address selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                selectedAddress = place.getAddress();
                selectedLocation = place.getLatLng();
                textViewSelectedAddress.setText(selectedAddress);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("AutocompleteError", status.getStatusMessage());
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle the user canceling the autocomplete
                Toast.makeText(this, "Address selection canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to submit the donation site
    private void submitDonationSite() {
        String name = editTextDonationSiteName.getText().toString();
        String donationHours = editTextDonationHours.getText().toString();
        String description = editTextDescription.getText().toString();

        if (name.isEmpty() || donationHours.isEmpty() || selectedAddress == null || description.isEmpty() || selectedLocation == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new DonationSite object
        DonationSite donationSite = new DonationSite(
                name,
                selectedAddress,
                donationHours,
                "",
                description,
                selectedLocation.latitude,
                selectedLocation.longitude);

        // Save the new donation site to Firestore or your database
        firestoreRepository.addDonationSite(donationSite);
        firestoreRepository.fetchBloodDonationSiteManagers().observe(this, bloodDonationSiteManagers -> {
            if (bloodDonationSiteManagers != null) {
                for (BloodDonationSiteManager manager : bloodDonationSiteManagers) {
                    if (manager.getEmail().equals(authenticationRepository.getCurrentUser().getEmail())) {
                        manager.getDonationSites().add(donationSite);
                        firestoreRepository.updateBloodDonationSiteManager(manager);
                        break;
                    }
                }
            }
        });


        Toast.makeText(this, "Donation Site Created Successfully", Toast.LENGTH_SHORT).show();

        // Optionally, finish the activity or redirect the user
        finish();
    }
}