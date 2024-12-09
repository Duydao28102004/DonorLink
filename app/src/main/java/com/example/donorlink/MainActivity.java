package com.example.donorlink;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Example data to add
        String donorName = "John Doe";
        String contactInfo = "john.doe@example.com";

        // Add data to Firestore
        addDonorData(donorName, contactInfo);
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

        public Donor() {}  // Firestore requires a no-argument constructor

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
}
