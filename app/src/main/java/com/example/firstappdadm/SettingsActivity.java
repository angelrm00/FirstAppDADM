package com.example.firstappdadm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.firstappdadm.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.containerSettings, SettingsFragment.class, null)
                .commit();

    }
}