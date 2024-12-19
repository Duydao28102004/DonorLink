package com.example.donorlink;

import android.content.Context;
import android.widget.Toast;

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
    private Context context;

    // Constructor
    public FirestoreRepository(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    // Create donor
    public void addDonor(Donor donor) {
        // Use the email as the document ID to enforce uniqueness
        db.collection("donors").document(donor.getEmail()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(context, "Donor with this email already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add the donor if the email doesn't exist
                        db.collection("donors").document(donor.getEmail())
                                .set(donor)
                                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Donor added successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(context, "Error adding donor", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error checking for existing donor", Toast.LENGTH_SHORT).show());
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
                    Toast.makeText(context, "Error fetching donors", Toast.LENGTH_SHORT).show();
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
                                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Donor updated successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(context, "Error updating donor", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(context, "No donor found with the given email", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error finding donor", Toast.LENGTH_SHORT).show());
    }

    // Delete donor
    public void deleteDonor(String email) {
        db.collection("donors")
                .document(email)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Donor deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error deleting donor", Toast.LENGTH_SHORT).show());
    }

    // Add Blood Donation Site Manager
    public void addBloodDonationSiteManager(BloodDonationSiteManager bloodDonationSiteManager) {
        // Use the email as the document ID to enforce uniqueness
        db.collection("bloodDonationSiteManagers").document(bloodDonationSiteManager.getEmail()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(context, "Blood Donation Site Manager with this email already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add the Blood Donation Site Manager if the email doesn't exist
                        db.collection("bloodDonationSiteManagers").document(bloodDonationSiteManager.getEmail())
                                .set(bloodDonationSiteManager)
                                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Blood Donation Site Manager added successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(context, "Error adding Blood Donation Site Manager", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error checking for existing Blood Donation Site Manager", Toast.LENGTH_SHORT).show());
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
                    Toast.makeText(context, "Error fetching Blood Donation Site Managers", Toast.LENGTH_SHORT).show();
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
                                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Blood Donation Site Manager updated successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(context, "Error updating Blood Donation Site Manager", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(context, "No Blood Donation Site Manager found with the given email", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error finding Blood Donation Site Manager", Toast.LENGTH_SHORT).show());
    }

    // Delete Blood Donation Site Manager
    public void deleteBloodDonationSiteManager(String email) {
        db.collection("bloodDonationSiteManagers")
                .document(email)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Blood Donation Site Manager deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error deleting Blood Donation Site Manager", Toast.LENGTH_SHORT).show());
    }

    // Add Donation Site
    public void addDonationSite(DonationSite donationSite) {
        // Use the name as the document ID to enforce uniqueness
        db.collection("donationSites").document(donationSite.getName()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(context, "Donation Site with this name already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add the Donation Site if the name doesn't exist
                        db.collection("donationSites").document(donationSite.getName())
                                .set(donationSite)
                                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Donation Site added successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(context, "Error adding Donation Site", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error checking for existing Donation Site", Toast.LENGTH_SHORT).show());
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
                    Toast.makeText(context, "Error fetching Donation Sites", Toast.LENGTH_SHORT).show();
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
                                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Donation Site updated successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(context, "Error updating Donation Site", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(context, "No Donation Site found with the given name", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error finding Donation Site", Toast.LENGTH_SHORT).show());
    }

    // Delete Donation Site
    public void deleteDonationSite(String name) {
        db.collection("donationSites")
                .document(name)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Donation Site deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error deleting Donation Site", Toast.LENGTH_SHORT).show());
    }
}