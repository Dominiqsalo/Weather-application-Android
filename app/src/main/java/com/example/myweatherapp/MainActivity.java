package com.example.myweatherapp;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.util.DisplayMetrics;
import com.example.myweatherapp.databinding.ActivityMainBinding;
import com.example.myweatherapp.ui.RainView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.android.volley.toolbox.Volley;
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int LOCATION_REQUEST_CODE = 1234;
    // Variables to store screen dimensions
    public static int screenWidth;
    public static int screenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the device's screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        RainView rainView = new RainView(this,screenWidth, screenHeight);

        // Set the size parameters
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        rainView.setLayoutParams(params);
        // Initialize ViewBinding and set the content view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Setup the bottom navigation view with NavController
        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,R.id.navigation_cities)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
// Initialize location manager and listener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Save the updated location data to shared preferences when location changes
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                SharedPreferences sharedPref=getSharedPreferences("WeatherAppPrefs",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPref.edit();
                Log.d("WeatherApp", "Saving Latitude: " + latitude + " and Longitude: " + longitude);
                editor.putFloat("Latitude",(float) latitude);
                editor.putFloat("Longitude",(float) longitude);
                editor.apply();
                Log.d("WeatherApp", "Location changed: Lat: " + latitude + " , Long: " + longitude);

            }
        @Override
            public void onStatusChanged(String provider, int status,Bundle extras){}

            @Override
            public void onProviderEnabled(String provider){}
        @Override
            public void onProviderDisabled(String provider){}
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,locationListener);
        }


    }
    // Handle the results of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchWeatherUsingLocation();
            } else {
                // Handle permission denial
                Toast.makeText(MainActivity.this,"Access denied",Toast.LENGTH_LONG).show();
            }
        }
    }
    // Request location updates if permission is granted
    private void fetchWeatherUsingLocation() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,locationListener);
        }
    }


    // Clean up any resources when the activity is destroyed
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            // Stop location updates to save battery and prevent memory leaks
            locationManager.removeUpdates(locationListener);
        }
    }


}

