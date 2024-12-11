package com.example.donorlink;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.donorlink.model.Donor;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirestoreRepository repository;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new FirestoreRepository();
        textView = findViewById(R.id.textView);

        // Fetch donors and observe changes
        LiveData<List<Donor>> donorLiveData = repository.fetchDonors();
        donorLiveData.observe(this, new Observer<List<Donor>>() {
            @Override
            public void onChanged(List<Donor> donors) {
                if (donors != null) {
                    StringBuilder donorList = new StringBuilder();
                    for (Donor donor : donors) {
                        donorList.append(donor.getName())
                                .append(" - ")
                                .append(donor.getName())
                                .append("\n");
                    }
                    textView.setText(donorList.toString());
                } else {
                    textView.setText("Error fetching donors");
                }
            }
        });
    }
}
