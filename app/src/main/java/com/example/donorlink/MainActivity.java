package com.example.donorlink;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MutableLiveData<List<Donor>> donorListLiveData = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Donor>> getDonorListLiveData() {
        return donorListLiveData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the textview
        TextView textView = findViewById(R.id.textView);

        // Example data to add
        String donorName = "John Doe";
        String contactInfo = "john.doe@example.com";

        // Add data to Firestore
//        addDonorData(donorName, contactInfo);

        // Fetch data from Firestore
        fetchDonorData();

        // Observe changes to the donor list
        getDonorListLiveData().observe(this, donors -> {
            String donorList = ""; // Reset the donor list string

            // Build the donor list string
            for (Donor donor : donors) {
                donorList += donor.getName() + " - " + donor.getContact() + "\n";
            }

            // Update the TextView with the donor list
            textView.setText(donorList);
        });
    }

    private void addDonorData(String name, String contact) {
        // Reference to the "donors" collection
        CollectionReference donorsCollection = db.collection("donors");

        // Create a new document with custom ID or auto-generated ID
        DocumentReference donorRef = donorsCollection.document(); // auto-generated ID

        // Create a map of the donor's data
        donorRef.set(new Donor(name, contact))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Donor data added successfully!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding donor data", e));
    }

    // Donor class to hold data
    public static class Donor {
        private String name;
        private String contact;

        public Donor() {
        }  // Firestore requires a no-argument constructor

        public Donor(String name, String contact) {
            this.name = name;
            this.contact = contact;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }
    }

    public void fetchDonorData() {
        CollectionReference donorsCollection = db.collection("donors");

        donorsCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Donor> resultDonor = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Donor donor = document.toObject(Donor.class);
                        if (donor != null) {
                            resultDonor.add(donor);
                        }
                    }
                    donorListLiveData.setValue(resultDonor); // Update LiveData
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching donor data", e);
                });
    }
}
