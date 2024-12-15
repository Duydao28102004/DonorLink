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

    private List<DonationSite> donationSites;

    public DonationSiteAdapter(List<DonationSite> donationSites) {
        this.donationSites = donationSites;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.donation_site, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonationSite site = donationSites.get(position);
        holder.siteNameTextView.setText(site.getName());
    }

    @Override
    public int getItemCount() {
        return donationSites != null ? donationSites.size() : 0;
    }

    // Method to update the data set dynamically
    public void updateData(List<DonationSite> newSites) {
        this.donationSites = newSites;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView siteNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            siteNameTextView = itemView.findViewById(R.id.siteName);
        }
    }
}