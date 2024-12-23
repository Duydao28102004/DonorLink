package com.example.donorlink;

import android.content.Context;
import android.content.Intent;
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
    private Context context;


    public DonationSiteAdapter(List<DonationSite> donationSites, Context context) {
        this.donationSites = donationSites;
        this.context = context;
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
        holder.siteAddressTextView.setText(site.getAddress());
        holder.siteHoursTextView.setText("Donation Hours: " + site.getDonationHours());
        // Set the click listener on the whole item
        holder.itemView.setOnClickListener(v -> {
            // Create an Intent to navigate to the DonationSiteDetailsActivity
            Intent intent = new Intent(context, DonationSiteDetailActivity.class);
            intent.putExtra("name", site.getName()); // Pass the DonationSite object
            context.startActivity(intent);
        });
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