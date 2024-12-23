package com.example.donorlink;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.donorlink.model.DonationSite;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class UpdateDonationSiteActivity extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private EditText editTextDonationSiteName, editTextDescription;
    private TextView textViewSelectedAddress;
    private Button buttonOpeningTime, buttonClosingTime, buttonSubmit, buttonSearchAddress;
    private LinearLayout bloodTypeLayout;

    private DonationSite donationSite; // The site being edited
    private List<String> selectedBloodTypes;
    private FirestoreRepository firestoreRepository;

    private String openingTime, closingTime;
    private String selectedAddress;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donation_site); // Reusing the same layout

        // Initialize Firestore repository
        firestoreRepository = new FirestoreRepository(this);

        // Initialize Views
        editTextDonationSiteName = findViewById(R.id.editTextDonationSiteName);
        buttonOpeningTime = findViewById(R.id.buttonOpeningTime);
        buttonClosingTime = findViewById(R.id.buttonClosingTime);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewSelectedAddress = findViewById(R.id.textViewSelectedAddress);
        buttonSearchAddress = findViewById(R.id.buttonSearchAddress);
        buttonSubmit = findViewById(R.id.buttonSubmitDonationSite);
        bloodTypeLayout = findViewById(R.id.linearLayoutBloodTypes);
        Button backButton = findViewById(R.id.buttonBack);

        backButton.setOnClickListener(v -> finish());

        // Retrieve the DonationSite object from the intent
        String donationSiteName = getIntent().getStringExtra("name");
        firestoreRepository.fetchDonationSites().observe(this, donationSites -> {
            if (donationSites != null) {
                for (DonationSite site : donationSites) {
                    if (site.getName().equals(donationSiteName)) {
                        populateFields(site);
                    }
                }
            }
        });

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAmYG0ewlmb4zaJAkC6pBsFjqi0NBQu-Po");
        }

        // Button click to open Autocomplete for address selection
        buttonSearchAddress.setOnClickListener(v -> openAutocomplete());

        // Handle time selection
        buttonOpeningTime.setOnClickListener(view -> openTimePickerDialog(true));
        buttonClosingTime.setOnClickListener(view -> openTimePickerDialog(false));


        // Save button functionality
        buttonSubmit.setText("Update Site");
        buttonSubmit.setOnClickListener(v -> {
            if (donationSite == null) {
                Toast.makeText(this, "Donation site data is not loaded. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            updateDonationSite();
        });
    }

    // Method to open TimePickerDialog for opening or closing time
    private void openTimePickerDialog(boolean isOpeningTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                UpdateDonationSiteActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                        // Format the time with hour and minute
                        String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);

                        if (isOpeningTime) {
                            openingTime = formattedTime;
                            buttonOpeningTime.setText("Opening Time: " + openingTime);
                        } else {
                            closingTime = formattedTime;
                            buttonClosingTime.setText("Closing Time: " + closingTime);
                        }
                    }
                },
                hour, minute, true
        );
        timePickerDialog.show();
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

    private void populateFields(DonationSite site) {
        donationSite = site;
        editTextDonationSiteName.setText(site.getName());
        editTextDescription.setText(site.getDescription());
        textViewSelectedAddress.setText(site.getAddress());
        selectedAddress = site.getAddress();
        selectedLocation = new LatLng(site.getLatitude(), site.getLongitude());

        // Parse donation hours
        String[] hours = site.getDonationHours().split(" - ");
        if (hours.length == 2) {
            openingTime = hours[0];
            closingTime = hours[1];
            buttonOpeningTime.setText("Opening Time: " + openingTime);
            buttonClosingTime.setText("Closing Time: " + closingTime);
        }

        // Populate blood type checkboxes
        selectedBloodTypes = site.getBloodType();
        String[] bloodTypes = getResources().getStringArray(R.array.blood_types);
        for (int i = 1; i < bloodTypes.length; i++) { // Skip the placeholder "Select Your Blood Type"
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(bloodTypes[i]);
            if (selectedBloodTypes.contains(bloodTypes[i])) {
                checkBox.setChecked(true);
            }
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedBloodTypes.add(buttonView.getText().toString());
                } else {
                    selectedBloodTypes.remove(buttonView.getText().toString());
                }
            });
            bloodTypeLayout.addView(checkBox);
        }
    }

    private void updateDonationSite() {
        String name = editTextDonationSiteName.getText().toString();
        String donationHours = openingTime + " - " + closingTime;
        String description = editTextDescription.getText().toString();

        // Use old values if fields are empty or not modified
        if (name.isEmpty()) {
            name = donationSite.getName();
        }

        if (openingTime == null || closingTime == null || openingTime.isEmpty() || closingTime.isEmpty()) {
            donationHours = donationSite.getDonationHours();
        }

        if (description.isEmpty()) {
            description = donationSite.getDescription();
        }

        if (selectedAddress == null) {
            selectedAddress = donationSite.getAddress();
            selectedLocation = new LatLng(donationSite.getLatitude(), donationSite.getLongitude());
        }

        if (selectedBloodTypes == null || selectedBloodTypes.isEmpty()) {
            selectedBloodTypes = donationSite.getBloodType();
        }

        // Check if all required fields are set
        if (name.isEmpty() || donationHours.isEmpty() || selectedAddress.isEmpty() || description.isEmpty() || selectedLocation == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedBloodTypes.isEmpty()) {
            Toast.makeText(this, "Please select at least one required blood type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the donation site object
        donationSite.setName(name);
        donationSite.setAddress(selectedAddress);
        donationSite.setDonationHours(donationHours);
        donationSite.setDescription(description);
        donationSite.setLatitude(selectedLocation.latitude);
        donationSite.setLongitude(selectedLocation.longitude);
        donationSite.setBloodType(selectedBloodTypes);

        // Save updated site to Firestore
        firestoreRepository.updateDonationSite(donationSite);

        Toast.makeText(this, "Donation Site Updated Successfully", Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }

}
