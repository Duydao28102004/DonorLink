package com.example.donorlink;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.donorlink.model.Donor;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FirestoreRepository {
    private FirebaseFirestore db;

    // Constructor
    public FirestoreRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Create donor
    public void addDonor(Donor donor) {
        db.collection("donors").document()  // Auto-generated ID
                .set(donor)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Donor added successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding donor", e));
    }

    // Fetch donors and return MutableLiveData
    public LiveData<List<Donor>> fetchDonors() {
        MutableLiveData<List<Donor>> donorsLiveData = new MutableLiveData<>();

        db.collection("donors").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Donor> donors = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Donor donor = document.toObject(Donor.class);
                        if (donor != null) {
                            donors.add(donor);
                        }
                    }
                    donorsLiveData.setValue(donors);  // Set the fetched data to LiveData
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching donors", e);
                    donorsLiveData.setValue(null);  // Optionally set null or handle error state
                });

        return donorsLiveData;
    }
}
