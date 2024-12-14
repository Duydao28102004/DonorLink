package com.example.donorlink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.donorlink.model.BloodDonationSiteManager;
import com.example.donorlink.model.DonationSite;
import com.example.donorlink.model.Donor;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AuthenticationRepository authRepository;
    private FirestoreRepository firestoreRepository;
    private ProgressBar loadingProgressBar;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authRepository = new AuthenticationRepository();
        firestoreRepository = new FirestoreRepository();
        // remove the top bar
        getSupportActionBar().hide();
        setLoginView();
    }

    private void setLogonView() {
        setContentView(R.layout.activity_signup);

        // Get username and email and password and re-enter password fields
        TextInputEditText usernameInput = findViewById(R.id.usernameInputText);
        TextInputEditText emailInput = findViewById(R.id.emailInputText);
        TextInputEditText passwordInput = findViewById(R.id.passwordInputText);
        TextInputEditText reEnterPasswordInput = findViewById(R.id.repasswordInputText);
        TextView signInTextView = findViewById(R.id.signInTextView);

        // Set up the onClickListener for the signUpTextView
        signInTextView.setOnClickListener(v -> {
            setLoginView();
        });

        // Get the user type switch
        SwitchCompat userTypeSwitch = findViewById(R.id.roleSwitch);
        AppCompatSpinner bloodTypeSpinner = findViewById(R.id.bloodTypeSpinner);

        userTypeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // If switch is checked (Site Manager), hide the blood type spinner
                bloodTypeSpinner.setVisibility(View.GONE);
            } else {
                // If switch is unchecked (Donor), show the blood type spinner
                bloodTypeSpinner.setVisibility(View.VISIBLE);
            }
        });

        // Initialize button and progress bar
        Button signUpButton = findViewById(R.id.signUpButton);
        ProgressBar loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Set sign up button click listener
        signUpButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String reEnterPassword = reEnterPasswordInput.getText().toString().trim();

            // Validate email and password and make sure it's not empty
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || reEnterPassword.isEmpty()) {
                Toast.makeText(MainActivity.this, "Email, password, and re-enter password are required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(MainActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(reEnterPassword)) {
                Toast.makeText(MainActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prevent default value of blood type spinner
            if (!userTypeSwitch.isChecked() && bloodTypeSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(MainActivity.this, "Please select a blood type", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show loading indicator and disable button
            loadingProgressBar.setVisibility(ProgressBar.VISIBLE);
            signUpButton.setEnabled(false);

            // Perform sign-up
            authRepository.signUp(email, password, new AuthenticationRepository.OnAuthCompleteListener() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    loadingProgressBar.setVisibility(ProgressBar.GONE);
                    signUpButton.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();

                    // Create the user in FireStore
                    if (userTypeSwitch.isChecked()) {
                        // Create a BloodDonationSiteManager
                        List<DonationSite> donationSiteList = new ArrayList<>();
                        BloodDonationSiteManager manager = new BloodDonationSiteManager(username, email, donationSiteList);
                        // Save the user to FireStore
                        firestoreRepository.addBloodDonationSiteManager(manager);
                    } else {
                        // Create a Donor user
                        String bloodType = bloodTypeSpinner.getSelectedItem().toString();
                        Donor donor = new Donor(username, email, bloodType);
                        // Save the user to FireStore
                        firestoreRepository.addDonor(donor);
                    }

                    // Navigate to home activity
                    setLoginView();
                }

                @Override
                public void onFailure(Exception exception) {
                    loadingProgressBar.setVisibility(ProgressBar.GONE);
                    signUpButton.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Sign Up Failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void setLoginView() {
        setContentView(R.layout.activity_login);

        // Get email and password fields
        TextInputEditText emailInput = findViewById(R.id.emailInputText);
        TextInputEditText passwordInput = findViewById(R.id.passwordInputText);

        // Initialize button and progress bar
        loginButton = findViewById(R.id.loginButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Set login button click listener
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate email and password and make sure it's not empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(MainActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show loading indicator and disable button
            showLoading(true);

            // Perform sign-in
            authRepository.signIn(email, password, new AuthenticationRepository.OnAuthCompleteListener() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    showLoading(false);
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    // Now fetch donor and manager data asynchronously
                    firestoreRepository.fetchDonors().observe(MainActivity.this, donors -> {
                        if (donors != null) {
                            for (Donor donor : donors) {
                                if (donor.getEmail().equals(user.getEmail())) {
                                    // Navigate to donor activity
                                    Intent intent = new Intent(MainActivity.this, DonorActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return;  // No need to check managers
                                }
                            }
                        }
                    });

                    firestoreRepository.fetchBloodDonationSiteManagers().observe(MainActivity.this, managers -> {
                        if (managers != null) {
                            for (BloodDonationSiteManager manager : managers) {
                                if (manager.getEmail().equals(user.getEmail())) {
                                    // Navigate to manager activity
                                    Intent intent = new Intent(MainActivity.this, BloodDonationSiteManagerActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return;  // No need to check donors
                                }
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Exception exception) {
                    showLoading(false);
                    Toast.makeText(MainActivity.this, "Login Failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // Set up the onClickListener for the signUpTextView
        TextView signUpTextView = findViewById(R.id.signUpTextView);
        signUpTextView.setOnClickListener(v -> {
            setLogonView();
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            loadingProgressBar.setVisibility(ProgressBar.VISIBLE);
            loginButton.setEnabled(false);
            loginButton.setText("");
        } else {
            loadingProgressBar.setVisibility(ProgressBar.GONE);
            loginButton.setEnabled(true);
            loginButton.setText("SIGN IN");
        }
    }
}
