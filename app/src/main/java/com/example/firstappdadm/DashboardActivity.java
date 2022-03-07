package com.example.firstappdadm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.firstappdadm.fragments.AboutFragment;
import com.example.firstappdadm.fragments.FavouriteFragment;
import com.example.firstappdadm.fragments.QuotationFragment;
import com.example.firstappdadm.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private Class<? extends Fragment> showFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView bar = findViewById(R.id.bottomNavigationView);
        bar.setOnItemSelectedListener( new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                actionInBar(item.getItemId());
                return true;
            }

            public void actionInBar(int Id) {
                switch (Id) {
                    case R.id.randomBar:
                        if(showFragment == QuotationFragment.class)
                            openSettings();
                        else {
                            showFragment = QuotationFragment.class;
                            getSupportActionBar().setTitle(getResources().getString(R.string.random_quotations));
                        }
                        break;
                    case R.id.favBar:
                        if(showFragment == FavouriteFragment.class)
                            openSettings();
                        else {
                            showFragment = FavouriteFragment.class;
                            getSupportActionBar().setTitle(getResources().getString(R.string.favourite_quotations));
                        }
                        break;
                    case R.id.settingsBar:
                        openSettings();
                        break;
                    case R.id.aboutBar:
                        if(showFragment == AboutFragment.class) {
                            openSettings();
                        } else {
                            showFragment = AboutFragment.class;
                            getSupportActionBar().setTitle(getResources().getString(R.string.about));
                        }
                        break;
                    default:
                        break;
                }
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainerView, showFragment, new Bundle()).commit();
            }
        });
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainerView, QuotationFragment.class, new Bundle()).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.randomBar:
                if(showFragment == QuotationFragment.class)
                    openSettings();
                else {
                    showFragment = QuotationFragment.class;
                    getSupportActionBar().setTitle(getResources().getString(R.string.random_quotations));
                }
                break;
            case R.id.favBar:
                if(showFragment == FavouriteFragment.class)
                    openSettings();
                else {
                    showFragment = FavouriteFragment.class;
                    getSupportActionBar().setTitle(getResources().getString(R.string.favourite_quotations));
                }
                break;
            case R.id.settingsBar:
                openSettings();
                break;
            case R.id.aboutBar:
                if(showFragment == AboutFragment.class) {
                    openSettings();
                } else {
                    showFragment = AboutFragment.class;
                    getSupportActionBar().setTitle(getResources().getString(R.string.about));
                }
                break;
            default:
                break;
        }
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainerView, showFragment, new Bundle()).commit();
    }

    public void openSettings() {
        showFragment = SettingsFragment.class;
        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
    }

}