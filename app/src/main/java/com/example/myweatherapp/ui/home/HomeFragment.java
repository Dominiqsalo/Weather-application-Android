package com.example.myweatherapp.ui.home;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import com.example.myweatherapp.CityAdapter;
import com.example.myweatherapp.R;
import com.example.myweatherapp.SearchCityActivity;
import com.example.myweatherapp.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.DisplayMetrics;

import com.example.myweatherapp.ui.AppDatabase;
import com.example.myweatherapp.ui.City;
import com.example.myweatherapp.ui.HourlyForecastAdapter;
import com.example.myweatherapp.ui.RainView;
import com.example.myweatherapp.ui.WeatherDay;
import com.example.myweatherapp.ui.WeatherHour;

public class HomeFragment extends Fragment implements CityAdapter.CityItemClickListener{

    // Variables for ViewBinding, RequestQueue for networking and a constant for location permission request code
    private FragmentHomeBinding binding;
    private RequestQueue queue;
    private String cityName = "";
    private HourlyForecastAdapter hourlyForecastAdapter;
    private List<WeatherHour> hourlyForecastData = new ArrayList<>();
    private static final int LOCATION_REQUEST_CODE = 1234;
    // Variables for different background colors based on the weather condition
    private int currentBackgroundColor = Color.parseColor("#FFFFFF"); // Keeping this to track the last dominant color
    int[] clearGradient = {Color.parseColor("#FFD9A5"), Color.parseColor("#e89700")};  // Gradient for clear weather
    int[] rainGradient = {Color.parseColor("#5C6BC0"), Color.parseColor("#9FA8DA")};   // Gradient for rainy weather
    int[] cloudsGradient = {Color.parseColor("#dedede"), Color.parseColor("#000000")}; // Gradient for cloudy weather

    int[] snowGradient = {Color.parseColor("#00FFFF"), Color.parseColor("#3c98e8")}; // Gradient for snowy weather

    int[] drizlGradient = {Color.parseColor("#5f84ad"), Color.parseColor("#143b66")}; // Gradient for drizzling weather

    int[] thundrGradient = {Color.parseColor("#007AC1"), Color.parseColor("#BF40BF")}; // Gradient for thunderous weather

    int[] atmosGradient = {Color.parseColor("#C2B280"), Color.parseColor("#808080")}; // Gradient for atmospheric weather

    boolean isCityDataFetched = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);  // This fragment has its own menu items to contribute
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_overflow, menu);  // Inflate the menu we created
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check which menu item was clicked
        if (item.getItemId() == R.id.action_search_city) {
            // Create an intent to start SearchCityActivity
            Intent intent = new Intent(getActivity(), SearchCityActivity.class);
            startActivityForResult(intent, LOCATION_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Transition background color and show appropriate weather icons based on the weather condition
    private void transitionBackground(String weatherCondition) {
        if (binding == null) return;

        ConstraintLayout rootLayout = getView().findViewById(R.id.containerr);
        int[] gradientColors = {};
        RainView rainView = getView().findViewById(R.id.rainViewId);
        ImageView weatherIcon = getView().findViewById(R.id.weather_icon);
        // Check the weather condition and change background, icon, and visibility accordingly
        switch (weatherCondition.toLowerCase()) {
            case "clear":
                weatherIcon.setImageResource(R.drawable.sunnny);
                gradientColors = clearGradient;
                rainView.setVisibility(View.GONE);
                break;
            case "rain":
                weatherIcon.setImageResource(R.drawable.rainy);
                gradientColors = rainGradient;
                rainView.setVisibility(View.VISIBLE);
                break;
            case "clouds":
                weatherIcon.setImageResource(R.drawable.cloudy);
                gradientColors = cloudsGradient;
                rainView.setVisibility(View.GONE);
                break;
            case "drizzle":
                weatherIcon.setImageResource(R.drawable.driz);
                gradientColors = drizlGradient;
                rainView.setVisibility(View.VISIBLE);
                break;

            case "thunderstorm":
                weatherIcon.setImageResource(R.drawable.thunder);
                gradientColors = thundrGradient;
                rainView.setVisibility(View.VISIBLE);
                break;
            case "atmosphere":
                weatherIcon.setImageResource(R.drawable.atmos);
                gradientColors = atmosGradient;
                rainView.setVisibility(View.GONE);
                break;
            case "snow":
                weatherIcon.setImageResource(R.drawable.snowy);
                gradientColors = snowGradient;
                rainView.setVisibility(View.GONE);
                break;
            default:
                weatherIcon.setVisibility(View.GONE);
                rainView.setVisibility(View.GONE);
                return;
        }

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                gradientColors
        );
        final TransitionDrawable crossfader = new TransitionDrawable(new Drawable[]{
                new ColorDrawable(currentBackgroundColor),
                gradientDrawable
        });
        // Animate the background color transition
        rootLayout.setBackground(crossfader);
        crossfader.startTransition(1000);

        currentBackgroundColor = gradientColors[1];
    }
    public void updateBackgroundColorBasedOnWeather(String weatherCondition) {
        if (binding == null) return;
        FrameLayout frameLayout = getView().findViewById(R.id.forecastframelayout);
        int[] gradientColors = {};

        switch(weatherCondition.toLowerCase()) {
            case "clear":
                gradientColors = clearGradient;
                break;
            case "rain":
                gradientColors = rainGradient;
                break;
            case "clouds":
                gradientColors = cloudsGradient;
                break;
            case "drizzle":
                gradientColors = drizlGradient;
                break;

            case "thunderstorm":
                gradientColors = thundrGradient;
                break;
            case "atmosphere":
                gradientColors = atmosGradient;
                break;
            case "snow":
                gradientColors = snowGradient;
                break;
            default:

                return;
        }
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                gradientColors
        );
        final TransitionDrawable crossfader = new TransitionDrawable(new Drawable[]{
                new ColorDrawable(currentBackgroundColor),
                gradientDrawable
        });
        // Animate the background color transition
        frameLayout.setBackground(crossfader);
        crossfader.startTransition(1000);

        currentBackgroundColor = gradientColors[1];
    }

    // Inflate the fragment's view and initialize its components
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Initialize the RequestQueue for networking
        queue = Volley.newRequestQueue(getContext());

        RecyclerView forecastLayout = root.findViewById(R.id.forecastLayout);

        Button addCityButton = root.findViewById(R.id.addCityButton);
        TextView cityNameTextView = root.findViewById(R.id.tv_city);



        if (getArguments() != null) {
            String cityNameArg = getArguments().getString("CITY_NAME");
            if (cityNameArg != null) {
                cityName = cityNameArg;
                cityNameTextView.setText(cityNameArg);
                fetchWeatherDataByCityName(cityNameArg);
                isCityDataFetched = true;

            }
        }

        hourlyForecastAdapter = new HourlyForecastAdapter(getContext(), hourlyForecastData, cityName);
        forecastLayout.setAdapter(hourlyForecastAdapter);

        Thread initialThread = new Thread(() -> {
            String currentCityName = cityNameTextView.getText().toString();
            AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, "weather-app-db").build();
            List<City> cities = db.cityDao().getCityByName(currentCityName);
            getActivity().runOnUiThread(() -> {
                if (cities != null && !cities.isEmpty()) {
                    addCityButton.setEnabled(false);
                    addCityButton.setText("Already Added");
                } else {
                    addCityButton.setEnabled(true);
                    addCityButton.setText("Add City");
                }
            });
        });
        initialThread.start();

        addCityButton.setOnClickListener(view -> {
            String cityNameFromText = cityNameTextView.getText().toString();
            cityName = cityNameFromText;
            City newCity = new City();
            newCity.name = cityNameFromText;

            Thread thread = new Thread(() -> {
                AppDatabase localDb = Room.databaseBuilder(getContext(), AppDatabase.class, "weather-app-db").build();
                List<City> existingCities = localDb.cityDao().getCityByName(cityName);
                if (existingCities==null || existingCities.isEmpty()){
                    localDb.cityDao().insert(newCity);
                    getActivity().runOnUiThread(() -> {
                        addCityButton.setEnabled(false);
                        addCityButton.setText("Already Added");
                    });
                }

            });
            thread.start();
        });

        // Fetching saved location data if available
        SharedPreferences sharedPref = getActivity().getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE);
        String savedCityName = sharedPref.getString("CityName", "");
        Log.d("DEBUG_CITY", "Retrieved city from prefs: " + savedCityName);
        float latitude = sharedPref.getFloat("Latitude", 0);
        float longitude = sharedPref.getFloat("Longitude", 0);

        if (!savedCityName.isEmpty()) {
            cityName = savedCityName;
        }
        if (getArguments() != null && cityName.isEmpty()) {
        }
        if (latitude != 0 && longitude != 0){
            fetchHourlyData(latitude, longitude);
        }

        if (!isCityDataFetched&&latitude != 0 && longitude != 0) {
            fetchWeatherData(latitude, longitude);
        }
// Initialize the RainView and add it to the layout
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        RainView rainView = new RainView(getContext(), screenWidth, screenHeight);
        rainView.setId(R.id.rainViewId);
        ScrollView scrollView = (ScrollView) binding.getRoot();
        ConstraintLayout layout = (ConstraintLayout) scrollView.getChildAt(0);
        layout.addView(rainView);


        return root;
    }

    private void updateAddCityButtonStatus(String cityName) {
        Thread thread = new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, "weather-app-db").build();
            List<City> cities = db.cityDao().getCityByName(cityName);
            getActivity().runOnUiThread(() -> {
                if (cities != null && !cities.isEmpty()) {
                    binding.addCityButton.setEnabled(false);
                    binding.addCityButton.setText("Already Added");
                } else {
                    binding.addCityButton.setEnabled(true);
                    binding.addCityButton.setText("Add City");
                }
            });
        });
        thread.start();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String cityName = data.getStringExtra("CITY_NAME");
            Log.d("DEBUG_CITY", "Setting cityName toactyvre: " + cityName);
            if (cityName != null && !cityName.isEmpty()) {
                fetchCoordinatesByCityName(cityName);
                fetchHourlyDataName(cityName);

            }
        }
    }

    //fetch coordinates based on city name
    private void fetchCoordinatesByCityName(String cityName) {
        String apiKey = "";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject coordObject = jsonObject.getJSONObject("coord");

                        double latitude = coordObject.getDouble("lat");
                        double longitude = coordObject.getDouble("lon");

                        // Now that you have the coordinates, you can use them as needed
                        fetchWeatherData(latitude, longitude);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing the data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error fetching data.", Toast.LENGTH_SHORT).show();
                });

        queue.add(stringRequest);
    }

    // Fetch weather data from the OpenWeatherMap API
    private void fetchWeatherData(double latitude, double longitude) {
        String apiKey = "";
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&apiKey=" + apiKey;

        Log.d("WeatherApp", "Fetching weather data from: " + url);
// Create a request to fetch data from the API and handle its response or any errors
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            // Here, parse the response string to extract weather details

            parseWeatherResponse(response);
        }, error -> {
            Log.e("WeatherApp", "Error fetching data: " + error.toString());
            // Handle errors
            Toast.makeText(getContext(), "Error fetching data.", Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);
    }

    // Parse the API response to extract weather details and update the UI
    private void parseWeatherResponse(String response) {
        String city= "";
        try {
            // Extract weather details from the JSON response
            JSONObject jsonObject = new JSONObject(response);
            city = jsonObject.getString("name");
            JSONObject main = jsonObject.getJSONObject("main");
            double temperature = main.getDouble("temp");

            String weatherCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
            transitionBackground(weatherCondition);
            updateBackgroundColorBasedOnWeather(weatherCondition);

            // Update the UI components with the parsed data
            TextView tvCity = getView().findViewById(R.id.tv_city);
            TextView tvTemperature = getView().findViewById(R.id.tv_temperature);
            TextView tvWeatherDescription = getView().findViewById(R.id.tv_weather_descriptionn);


            tvCity.setText(city);
            cityName = city;
            Log.d("DEBUG_CITY", "Setting cityName to parse: " + cityName);
            if (hourlyForecastAdapter != null) {
                hourlyForecastAdapter.setCityName(cityName);
            }


            tvTemperature.setText(String.format(Locale.getDefault(), "%.1f°C", temperature - 273.15));
            tvWeatherDescription.setText(weatherDescription);
// Check weather condition to decide if the rain animation should be shown or hidden
            if ("Rain".equalsIgnoreCase(weatherCondition)) {
                displayRainAnimation();
            } else {
                hideRainAnimation();
            }
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(city);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("WeatherApp", "Error parsing the weather data: " + e.toString());
            return;
        }
        updateAddCityButtonStatus(city);
    }

    private void fetchWeatherDataByCityName(String cityName) {

        String apiKey = "";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        double latitude = jsonObject.getJSONObject("coord").getDouble("lat");
                        double longitude = jsonObject.getJSONObject("coord").getDouble("lon");

                        // Use the fetched coordinates to get more detailed weather data
                        fetchWeatherData(latitude, longitude);
                        fetchHourlyData(latitude, longitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing the data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error fetching data.", Toast.LENGTH_SHORT).show();
                });

        queue.add(stringRequest);
    }

    private void fetchHourlyDataName(String cityName) {
        String apiKey = "";
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&apiKey=" + apiKey;

        fetchHourlyDataFromURL(url);
    }
    private void fetchHourlyData(double latitude,double longitude) {
        String apiKey = "";
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&apiKey=" + apiKey;

        fetchHourlyDataFromURL(url);
    }
    private void fetchHourlyDataFromURL(String url) {
        Log.d("WeatherApp", "Fetching hourly forecast data from: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            // Here, parse the response string to extract hourly forecast details
            Log.d("WeatherApp", "API Response: " + response);
            parseHourlyResponse(response);
        }, error -> {
            Log.e("WeatherApp", "Error fetching hourly data: " + error.toString());
            Toast.makeText(getContext(), "Error fetching hourly data.", Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);
    }
    private void parseHourlyResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray hourlyArray = jsonObject.getJSONArray("list");
            hourlyForecastData.clear();
            for (int i = 0; i < hourlyArray.length(); i++) {
                JSONObject forecast=hourlyArray.getJSONObject(i);

                long unixTimestamp = forecast.getLong("dt");
                Date date = new Date(unixTimestamp * 1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm EEEE", Locale.US);

                String time = sdf.format(date);
                String condition= forecast.getJSONArray("weather").getJSONObject(0).getString("main");
                int temperature=(int)(forecast.getJSONObject("main").getDouble("temp")-273.15);
                double rawTemp = forecast.getJSONObject("main").getDouble("temp");
                Log.d("WeatherApp", "Raw Temperature for day " + time + ": " + rawTemp);
                hourlyForecastData.add(new WeatherHour(time,condition,temperature));

            }
            hourlyForecastAdapter.updateData(hourlyForecastData);
            hourlyForecastAdapter.notifyDataSetChanged();
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.e("WeatherApp", "Error parsing the 3-hour data: " + e.toString());
        }
    }
    // Display the rain animation
    private void displayRainAnimation() {
        ScrollView scrollView = (ScrollView) binding.getRoot();
        ConstraintLayout layout = (ConstraintLayout) scrollView.getChildAt(0);
        RainView rainView = layout.findViewById(R.id.rainViewId);
        if (rainView != null) {
            rainView.setVisibility(View.VISIBLE);
        }
    }

    // Hide the rain animation by removing the RainView from the layout
    private void hideRainAnimation() {
        ScrollView scrollView = (ScrollView) binding.getRoot();
        ConstraintLayout layout = (ConstraintLayout) scrollView.getChildAt(0);
        RainView rainView = layout.findViewById(R.id.rainViewId);
        if (rainView != null) {
            rainView.setVisibility(View.GONE);
        }
    }
    @Override
    public void onCityClick(String cityName) {
        fetchHourlyDataName(cityName);
    }

    @Override
    public void onDeleteCityClick(String cityName) {
        // Handle city delete action if needed
    }
 
    // Clean up any resources when the view is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}