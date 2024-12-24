package com.example.donorlink;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactUsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // Optionally, set up a toolbar or back button
        getSupportActionBar().hide();

        // get back button and set on click event
        findViewById(R.id.buttonBack).setOnClickListener(v -> {
            finish();
        });

    }
}
