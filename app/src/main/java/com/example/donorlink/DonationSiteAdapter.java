package com.example.donorlink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorlink.model.DonationSite;

import java.util.List;

public class DonationSiteAdapter extends RecyclerView.Adapter<DonationSiteAdapter.ViewHolder> {

    private List<DonationSite> donationSites;  // The list of donation sites

    // Constructor
    public DonationSiteAdapter(List<DonationSite> donationSites) {
        this.donationSites = donationSites;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.donation_site, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the ViewHolder at the given position in the list
        DonationSite site = donationSites.get(position);
        holder.siteNameTextView.setText(site.getName());
        holder.siteAddressTextView.setText(site.getAddress());
        holder.donationHoursTextView.setText(site.getDonationHours());
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the list
        return donationSites != null ? donationSites.size() : 0;
    }

    // ViewHolder class for holding the individual views for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView siteNameTextView;
        TextView siteAddressTextView;
        TextView donationHoursTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views in the item layout
            siteNameTextView = itemView.findViewById(R.id.siteName);
            siteAddressTextView = itemView.findViewById(R.id.siteAddress);
            donationHoursTextView = itemView.findViewById(R.id.donationHours);
        }
    }
}