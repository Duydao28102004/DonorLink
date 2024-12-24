package com.example.donorlink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorlink.model.Donor;
import com.example.donorlink.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notificationList;
    private Context context;
    private FirestoreRepository firestoreRepository;
    private AuthenticationRepository authenticationRepository;

    public NotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
        firestoreRepository = new FirestoreRepository(context);  // Initialize FirestoreRepository
        authenticationRepository = new AuthenticationRepository(); // Initialize AuthenticationRepository
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.titleTextView.setText(notification.getTitle());
        holder.messageTextView.setText(notification.getMessage());

        // Set a listener to delete the notification
        holder.deleteButton.setOnClickListener(v -> {
            deleteNotification(position);
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    private void deleteNotification(int position) {
        // Get the notification to be deleted
        Notification notification = notificationList.get(position);

        firestoreRepository.fetchDonors().observe((LifecycleOwner) context, donors -> {
            if (donors != null) {
                for (Donor donor : donors) {
                    if (donor.getEmail().equals(authenticationRepository.getCurrentUser().getEmail())) {
                        for (int i = 0; i < donor.getNotificationList().size(); i++) {
                            if (donor.getNotificationList().get(i).getTitle().equals(notification.getTitle())) {
                                donor.getNotificationList().remove(i);
                                break;
                            }
                        }
                        firestoreRepository.updateDonor(donor);  // Make sure the Firestore repository properly updates the donor
                        notificationList.remove(position);  // Remove the notification from the list
                        notifyItemRemoved(position);  // Notify adapter about the change
                        break;
                    }
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView messageTextView;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notificationTitle);
            messageTextView = itemView.findViewById(R.id.notificationMessage);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }
}
