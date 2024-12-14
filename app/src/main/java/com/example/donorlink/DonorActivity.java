package com.example.donorlink;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.donorlink.donorfragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DonorActivity extends AppCompatActivity {
    private FirestoreRepository firestoreRepository;
    private AuthenticationRepository authenticationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donor);
        getSupportActionBar().hide();

        firestoreRepository = new FirestoreRepository();
        authenticationRepository = new AuthenticationRepository();


        // Create a Bundle with the email argument
        Bundle args = new Bundle();
        String email = authenticationRepository.getCurrentUser().getEmail();
        args.putString("email", email);

        // Load the initial HomeFragment with the arguments
        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
            homeFragment.setArguments(args);
            loadFragment(homeFragment);
        }

        // Initialize the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up the BottomNavigationView to switch between fragments
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                selectedFragment.setArguments(args);
            } else if (itemId == R.id.nav_map) {
//                selectedFragment = new MapFragment();
            } else if (itemId == R.id.nav_profile) {
//                selectedFragment = new ProfileFragment();
            } else {
                return false;
            }

            return loadFragment(selectedFragment);
        });
    }

    // Helper method to load fragments
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}