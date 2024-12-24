package com.example.donorlink;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorlink.model.Donor;
import com.example.donorlink.model.Notification;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    private FirestoreRepository firestoreRepository;
    private AuthenticationRepository authenticationRepository;
    private TextView noNotificationsText; // Declare the TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().hide();

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noNotificationsText = findViewById(R.id.noNotificationsText); // Initialize the TextView

        // get back button and set click listener to kill this activity
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());

        firestoreRepository = new FirestoreRepository(this);
        authenticationRepository = new AuthenticationRepository();

        // Fetch notifications from Firestore and update the UI
        fetchNotifications();

        // Set up the button to delete all notifications
        findViewById(R.id.btnDeleteAll).setOnClickListener(v -> deleteAllNotifications());
    }

    private void fetchNotifications() {
        firestoreRepository.fetchDonors().observe(NotificationActivity.this, donors -> {
            if (donors != null) {
                // Clear previous notifications before adding new ones
                notificationList.clear();

                for (Donor donor : donors) {
                    if (donor.getEmail().equals(authenticationRepository.getCurrentUser().getEmail())) {
                        // Ensure donor's notification list is not null
                        if (donor.getNotificationList() != null) {
                            notificationList.addAll(donor.getNotificationList());
                        }
                        break;  // Exit the loop once the current donor is found
                    }
                }

                // Update the adapter
                if (notificationAdapter == null) {
                    notificationAdapter = new NotificationAdapter(notificationList, NotificationActivity.this);
                    recyclerView.setAdapter(notificationAdapter);
                } else {
                    notificationAdapter.notifyDataSetChanged();
                }

                // Show/hide "No notifications" message
                toggleNoNotificationsMessage();
            }
        });
    }

    private void deleteAllNotifications() {
        firestoreRepository.fetchDonors().observe(NotificationActivity.this, donors -> {
            if (donors != null) {
                for (Donor donor : donors) {
                    if (donor.getEmail().equals(authenticationRepository.getCurrentUser().getEmail())) {
                        donor.setNotificationList(new ArrayList<>());
                        firestoreRepository.updateDonor(donor); // Update Firestore with the empty notification list
                        notificationList.clear(); // Clear the local list
                        notificationAdapter.notifyDataSetChanged(); // Notify the adapter
                        break;
                    }
                }
            }

            // Show/hide "No notifications" message
            toggleNoNotificationsMessage();
        });
    }

    private void toggleNoNotificationsMessage() {
        // Show "No notifications" message if the list is empty, else hide it
        if (notificationList.isEmpty()) {
            noNotificationsText.setVisibility(View.VISIBLE);
        } else {
            noNotificationsText.setVisibility(View.GONE);
        }
    }
}
