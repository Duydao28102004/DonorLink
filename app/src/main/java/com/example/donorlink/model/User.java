package com.example.donorlink.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String email;
    private List<Notification> notificationList;

    public User() {}  // Firestore requires a no-argument constructor

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        notificationList = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }
}
