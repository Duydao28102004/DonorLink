package com.example.donorlink.donorfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.donorlink.R;
import com.example.donorlink.model.DonationSite;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<DonationSite> donationSites;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DonationSite donationSite);
    }

    public SearchResultsAdapter(List<DonationSite> donationSites, OnItemClickListener listener) {
        this.donationSites = donationSites;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation_site, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DonationSite site = donationSites.get(position);
        holder.siteName.setText(site.getName());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(site));
    }

    @Override
    public int getItemCount() {
        return donationSites.size();
    }

    public void updateData(List<DonationSite> newDonationSites) {
        donationSites = newDonationSites;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView siteName;

        public ViewHolder(View itemView) {
            super(itemView);
            siteName = itemView.findViewById(R.id.siteName);
        }
    }
}