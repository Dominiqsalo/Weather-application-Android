package com.example.myweatherapp.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myweatherapp.R;
import com.example.myweatherapp.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WeatherDisplayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_display);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String cityName = getIntent().getStringExtra("CITY_NAME");
        // Use the city name to fetch and display the weather data
        Bundle bundle = new Bundle();
        bundle.putString("CITY_NAME", cityName);

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button.
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
