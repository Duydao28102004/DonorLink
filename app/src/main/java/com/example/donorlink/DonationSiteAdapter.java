package com.example.donorlink;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorlink.model.DonationSite;

import java.util.ArrayList;
import java.util.List;

public class DonationSiteAdapter extends RecyclerView.Adapter<DonationSiteAdapter.ViewHolder> {

    private List<DonationSite> donationSites;
    private List<DonationSite> filteredSites;
    private Context context;
    private TextView noDonationSitesTextView;

    public DonationSiteAdapter(List<DonationSite> donationSites, Context context) {
        this.donationSites = donationSites;
        this.filteredSites = new ArrayList<>(donationSites);
        this.context = context;
    }

    public DonationSiteAdapter(List<DonationSite> donationSites, Context context, TextView noDonationSitesTextView) {
        this.donationSites = donationSites;
        this.filteredSites = new ArrayList<>(donationSites);
        this.context = context;
        this.noDonationSitesTextView = noDonationSitesTextView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_site, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonationSite site = filteredSites.get(position);
        holder.siteNameTextView.setText(site.getName());
        holder.siteAddressTextView.setText(site.getAddress());
        holder.siteHoursTextView.setText("Donation Hours: " + site.getDonationHours());

        holder.itemView.setOnClickListener(v -> {
            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;
                Intent intent = new Intent(activity, DonationSiteDetailActivity.class);
                intent.putExtra("name", site.getName());
                activity.startActivityForResult(intent, 1001);
            }
        });
    }

    @Override
    public int getItemCount() {
        // If no filtered sites, show "No results found"
        if (filteredSites.isEmpty()) {
            if (noDonationSitesTextView != null) {
                noDonationSitesTextView.setVisibility(View.VISIBLE); // Show the "No sites found" message
            }
        } else {
            if (noDonationSitesTextView != null) {
                noDonationSitesTextView.setVisibility(View.GONE); // Hide the message
            }
        }
        return filteredSites.size();  // Always return the size of filteredSites
    }

    // Method to update the data set dynamically
    public void updateData(List<DonationSite> newSites) {
        this.donationSites = newSites;
        this.filteredSites = new ArrayList<>(newSites); // Update the filtered list as well
        notifyDataSetChanged();
    }

    public void filter(String query) {
        List<DonationSite> filteredList = new ArrayList<>();

        // Check if the query is empty or null and reset to full list
        if (query == null || query.isEmpty()) {
            filteredList.addAll(donationSites); // Reset to the full list if query is empty
        } else {
            for (DonationSite site : donationSites) {
                if (site.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(site);
                }
            }
        }

        filteredSites = filteredList; // Update the filtered list
        notifyDataSetChanged(); // Notify RecyclerView to refresh
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView siteNameTextView;
        TextView siteAddressTextView;
        TextView siteHoursTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            siteNameTextView = itemView.findViewById(R.id.name);
            siteAddressTextView = itemView.findViewById(R.id.address);
            siteHoursTextView = itemView.findViewById(R.id.donationHours);
        }
    }
}
