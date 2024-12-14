package com.example.donorlink;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.donorlink.model.BloodDonationSiteManager;
import com.example.donorlink.model.DonationSite;
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
        // Use the email as the document ID to enforce uniqueness
        db.collection("donors").document(donor.getEmail()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.e("Firestore", "Donor with this email already exists");
                    } else {
                        // Add the donor if the email doesn't exist
                        db.collection("donors").document(donor.getEmail())
                                .set(donor)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Donor added successfully"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Error adding donor", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error checking for existing donor", e));
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

    // Update donor
    public void updateDonor(Donor donor) {
        db.collection("donors")
                .whereEqualTo("email", donor.getEmail())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Loop through the matching documents and update them
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            document.getReference()
                                    .set(donor)  // Set the updated donor data
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Donor updated successfully"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating donor", e));
                        }
                    } else {
                        Log.e("Firestore", "No donor found with the given email");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error finding donor", e));
    }

    // Delete donor
    public void deleteDonor(String email) {
        db.collection("donors")
                .document(email)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Donor deleted successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting donor", e));
    }

    // Add Blood Donation Site Manager
    public void addBloodDonationSiteManager(BloodDonationSiteManager bloodDonationSiteManager) {
        // Use the email as the document ID to enforce uniqueness
        db.collection("bloodDonationSiteManagers").document(bloodDonationSiteManager.getEmail()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.e("Firestore", "Blood Donation Site Manager with this email already exists");
                    } else {
                        // Add the Blood Donation Site Manager if the email doesn't exist
                        db.collection("bloodDonationSiteManagers").document(bloodDonationSiteManager.getEmail())
                                .set(bloodDonationSiteManager)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Blood Donation Site Manager added successfully"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Error adding Blood Donation Site Manager", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error checking for existing Blood Donation Site Manager", e));
    }

    // Fetch Blood Donation Site Managers and return MutableLiveData
    public LiveData<List<BloodDonationSiteManager>> fetchBloodDonationSiteManagers() {
        MutableLiveData<List<BloodDonationSiteManager>> bloodDonationSiteManagersLiveData = new MutableLiveData<>();

        db.collection("bloodDonationSiteManagers").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<BloodDonationSiteManager> bloodDonationSiteManagers = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        BloodDonationSiteManager bloodDonationSiteManager = document.toObject(BloodDonationSiteManager.class);
                        if (bloodDonationSiteManager != null) {
                            bloodDonationSiteManagers.add(bloodDonationSiteManager);
                        }
                    }
                    bloodDonationSiteManagersLiveData.setValue(bloodDonationSiteManagers);  // Set the fetched data to LiveData
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching Blood Donation Site Managers", e);
                    bloodDonationSiteManagersLiveData.setValue(null);  // Optionally set null or handle error state
                });

        return bloodDonationSiteManagersLiveData;
    }

    // Update Blood Donation Site Manager
    public void updateBloodDonationSiteManager(BloodDonationSiteManager bloodDonationSiteManager) {
        db.collection("bloodDonationSiteManagers")
                .whereEqualTo("email", bloodDonationSiteManager.getEmail())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Loop through the matching documents and update them
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            document.getReference()
                                    .set(bloodDonationSiteManager)  // Set the updated Blood Donation Site Manager data
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Blood Donation Site Manager updated successfully"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating Blood Donation Site Manager", e));
                        }
                    } else {
                        Log.e("Firestore", "No Blood Donation Site Manager found with the given email");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error finding Blood Donation Site Manager", e));
    }

    // Delete Blood Donation Site Manager
    public void deleteBloodDonationSiteManager(String email) {
        db.collection("bloodDonationSiteManagers")
                .document(email)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Blood Donation Site Manager deleted successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting Blood Donation Site Manager", e));
    }

    // Add Donation Site
    public void addDonationSite(DonationSite donationSite) {
        // Use the name as the document ID to enforce uniqueness
        db.collection("donationSites").document(donationSite.getName()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.e("Firestore", "Donation Site with this name already exists");
                    } else {
                        // Add the Donation Site if the name doesn't exist
                        db.collection("donationSites").document(donationSite.getName())
                                .set(donationSite)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Donation Site added successfully"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Error adding Donation Site", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error checking for existing Donation Site", e));
    }

    // Fetch Donation Sites and return MutableLiveData
    public LiveData<List<DonationSite>> fetchDonationSites() {
        MutableLiveData<List<DonationSite>> donationSitesLiveData = new MutableLiveData<>();

        db.collection("donationSites").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DonationSite> donationSites = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        DonationSite donationSite = document.toObject(DonationSite.class);
                        if (donationSite != null) {
                            donationSites.add(donationSite);
                        }
                    }
                    donationSitesLiveData.setValue(donationSites);  // Set the fetched data to LiveData
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching Donation Sites", e);
                    donationSitesLiveData.setValue(null);  // Optionally set null or handle error state
                });

        return donationSitesLiveData;
    }

    // Update Donation Site
    public void updateDonationSite(DonationSite donationSite) {
        db.collection("donationSites")
                .whereEqualTo("name", donationSite.getName())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Loop through the matching documents and update them
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            document.getReference()
                                    .set(donationSite)  // Set the updated Donation Site data
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Donation Site updated successfully"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating Donation Site", e));
                        }
                    } else {
                        Log.e("Firestore", "No Donation Site found with the given name");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error finding Donation Site", e));
    }

    // Delete Donation Site
    public void deleteDonationSite(String name) {
        db.collection("donationSites")
                .document(name)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Donation Site deleted successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting Donation Site", e));
    }
}
