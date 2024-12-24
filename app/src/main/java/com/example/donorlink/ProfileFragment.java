package com.example.donorlink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.donorlink.model.BloodDonationSiteManager;
import com.example.donorlink.model.Donor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        FirestoreRepository firestoreRepository = new FirestoreRepository(getContext());
        AuthenticationRepository authenticationRepository = new AuthenticationRepository();
        TextView nameTextView = view.findViewById(R.id.nameText);
        TextView roleTextView = view.findViewById(R.id.roleText);
        TextView emailTextView = view.findViewById(R.id.emailText);
        TextView bloodTypeTextView = view.findViewById(R.id.bloodTypeText);

        firestoreRepository.fetchBloodDonationSiteManagers().observe(getViewLifecycleOwner(), bloodDonationSiteManagers -> {
                    if (bloodDonationSiteManagers != null) {
                        for (BloodDonationSiteManager bloodDonationSiteManager : bloodDonationSiteManagers) {
                            if (bloodDonationSiteManager.getEmail().equals(authenticationRepository.getCurrentUser().getEmail())) {
                                nameTextView.setText(bloodDonationSiteManager.getUsername());
                                roleTextView.setText("Blood Donation Site Manager");
                                emailTextView.setText(bloodDonationSiteManager.getEmail());
                                bloodTypeTextView.setVisibility(View.GONE);
                            }
                        }
                    }
                });
        firestoreRepository.fetchDonors().observe(getViewLifecycleOwner(), donors -> {
            if (donors != null) {
                for (Donor donor : donors) {
                    if (donor.getEmail().equals(authenticationRepository.getCurrentUser().getEmail())) {
                        nameTextView.setText(donor.getUsername());
                        roleTextView.setText("Donor");
                        emailTextView.setText(donor.getEmail());
                        bloodTypeTextView.setText("Blood Type: " + donor.getBloodType());
                    }
                }
            }
        });

        // Get buttons and set onClickListeners
        Button aboutUsButton = view.findViewById(R.id.aboutUsButton);
        Button faqButton = view.findViewById(R.id.faqButton);
        Button contactUsButton = view.findViewById(R.id.contactUsButton);
        Button logoutButton = view.findViewById(R.id.logoutButton);

        aboutUsButton.setOnClickListener(v -> navigateToActivity(AboutUsActivity.class));
        faqButton.setOnClickListener(v -> navigateToActivity(FaqActivity.class));
        contactUsButton.setOnClickListener(v -> navigateToActivity(ContactUsActivity.class));
        logoutButton.setOnClickListener(v -> logoutUser(getActivity()));

        return view;
    }


    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(getActivity(), activityClass);
        startActivity(intent);
    }

    private void logoutUser(Context context) {
        // Create an instance of the AuthenticationRepository
        AuthenticationRepository authRepository = new AuthenticationRepository();

        // Call the logout method on the AuthenticationRepository
        authRepository.signOut();

        // Navigate back to MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
        context.startActivity(intent);

        // Optionally finish the current activity (if you're in an activity and not just a fragment)
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}