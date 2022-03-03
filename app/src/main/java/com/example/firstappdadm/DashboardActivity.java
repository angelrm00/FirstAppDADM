package com.example.firstappdadm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        findViewById(R.id.randomButton).setOnClickListener(this);
        findViewById(R.id.favouriteButton).setOnClickListener(this);
        findViewById(R.id.settingsButton).setOnClickListener(this);
        findViewById(R.id.aboutButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.randomButton:
                startActivity(new Intent(this, QuotationActivity.class));
                break;
            case R.id.favouriteButton:
                startActivity(new Intent(this, FavouriteActivity.class));
                break;
            case R.id.settingsButton:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.aboutButton:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            default:
                break;
        }
    }
}