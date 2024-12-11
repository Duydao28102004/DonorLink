package com.example.donorlink.model;

public class Notification {
    private String title;
    private String message;

    public Notification() {}  // Firestore requires a no-argument constructor

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
