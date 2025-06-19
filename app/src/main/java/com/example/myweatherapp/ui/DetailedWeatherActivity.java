package com.example.myweatherapp.ui;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myweatherapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailedWeatherActivity extends AppCompatActivity {
    private RequestQueue queue;
    private RecyclerView recyclerView;
    private ForecastAdapter forecastAdapter;
    private List<WeatherDay> forecastData=new ArrayList<>();
    private String cityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_weather);

        queue = Volley.newRequestQueue(this);

        String cityName = getIntent().getStringExtra("cityName");
        fetchWeatherDataByCityName(cityName);
        recyclerView=findViewById(R.id.recycler_weekly_forecast);
        LinearLayoutManager layoutManager=(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setLayoutManager(layoutManager);
        forecastAdapter=new ForecastAdapter(this,forecastData,cityName);
        recyclerView.setAdapter(forecastAdapter);


        SharedPreferences sharedPref = getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE);
        fetchForecastDataByCityName(cityName);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    // Method to fetch forecast data from the OpenWeatherMap API
    private void fetchForecastData(double latitude,double longitude){
        // Retrieve saved coordinates from shared preferences
        SharedPreferences sharedPreferences= getSharedPreferences("WeatherApp",Context.MODE_PRIVATE);

        // Construct the API endpoint URL
        String apiKey="";
        String url="https://api.openweathermap.org/data/2.5/forecast?lat="+ latitude + "&lon="+ longitude + "&appid="+apiKey;

        // Send the request and handle the response
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, response -> {
            parseForecastResponse(response);
        }, error -> {
            Log.e("WeatherApp","Error fetching forecast data: "+error.toString());
        });

        queue.add(stringRequest);
    }
    private void fetchForecastDataByCityName(String cityName){
        String apiKey="";
        String url="https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&appid="+apiKey;

        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, response -> {
            parseForecastResponse(response);
        }, error -> {
            Log.e("WeatherApp","Error fetching forecast data: "+error.toString());
        });

        queue.add(stringRequest);
    }
    // Parse the forecast API response and update the RecyclerView's dataset
    private void parseForecastResponse(String response){
        String city= "";
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray forecastList= jsonObject.getJSONArray("list");
            forecastData.clear();
            // Iterate over the forecast data in 8-hour intervals and extract relevant information
            for (int i=0; i<forecastList.length();i+=8){
                JSONObject forecast=forecastList.getJSONObject(i);

                long unixTimestamp = forecast.getLong("dt");
                Date date = new Date(unixTimestamp * 1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.US); // EEEE gives the full day name

                String day = sdf.format(date);
                String condition= forecast.getJSONArray("weather").getJSONObject(0).getString("main");
                int temperature=(int)(forecast.getJSONObject("main").getDouble("temp")-273.15);
                double rawTemp = forecast.getJSONObject("main").getDouble("temp");

                forecastData.add(new WeatherDay(day,condition,temperature));

            }
            cityName = city;
            if (forecastAdapter != null) {
                forecastAdapter.setCityName(cityName);
            }
            forecastAdapter.updateData(forecastData);
            forecastAdapter.notifyDataSetChanged();
        }catch (Exception e){
            Log.e("WeatherApp", "Error parsing forecast data: " + e.toString());
        }
    }

    private void fetchWeatherDataByCityName(String cityName) {
        String apiKey = "";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> parseWeatherResponse(response),
                error -> Toast.makeText(DetailedWeatherActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }
    private void parseWeatherResponse(String response) {
        try {
            // Extract weather details from the JSON response
            JSONObject jsonObject = new JSONObject(response);
            JSONObject main = jsonObject.getJSONObject("main");


            double temperature = main.getDouble("temp");
            double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
            long sunUp = jsonObject.getJSONObject("sys").getLong("sunrise");
            long sunDown = jsonObject.getJSONObject("sys").getLong("sunset");
            double maxTemp = main.getDouble("temp_max");
            double minTemp = main.getDouble("temp_min");
            double feelsLike = main.getDouble("feels_like");
            int humidity = main.getInt("humidity");
            int visibility = jsonObject.getInt("visibility");
            double maxTempInCelsius = maxTemp - 273.15;
            double minTempInCelsius = minTemp - 273.15;
            double feelsLikeInCelsius = feelsLike - 273.15;
            double visibilityInKm = visibility / 1000.0;

            TextView tvTemp = findViewById(R.id.tv_temp);
            TextView tvWeatherDescription = findViewById(R.id.tv_weather_desc);
            TextView tvWindSpeed = findViewById(R.id.tv_wind_speed);
            TextView tvSunUp = findViewById(R.id.tv_sun_up);
            TextView tvSunDown = findViewById(R.id.tv_sun_down);
            TextView tvMaxTemp = findViewById(R.id.tv_max_temp);
            TextView tvMinTemp = findViewById(R.id.tv_min_temp);
            TextView tvFeelsLike = findViewById(R.id.tv_feels_like);
            TextView tvHumidity = findViewById(R.id.tv_humidity);
            TextView tvVisibility = findViewById(R.id.tv_visibility);

            String weatherCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

            tvTemp.setText(String.format(Locale.getDefault(), "%.1f°C", temperature - 273.15));
            tvWeatherDescription.setText(weatherDescription);
            tvWindSpeed.setText(String.format(Locale.getDefault(), "%.2f m/s", windSpeed));
            tvSunUp.setText(new Date(sunUp * 1000L).toString());
            tvSunDown.setText(new Date(sunDown * 1000L).toString());
            tvMaxTemp.setText(String.format(Locale.getDefault(), "%.1f°C", maxTempInCelsius));
            tvMinTemp.setText(String.format(Locale.getDefault(), "%.1f°C", minTempInCelsius));
            tvFeelsLike.setText(String.format(Locale.getDefault(), "%.1f°C", feelsLikeInCelsius));
            tvHumidity.setText(String.format(Locale.getDefault(), "%d%%", humidity));
            tvVisibility.setText(String.format(Locale.getDefault(), "%.2f km", visibilityInKm));

        }
        catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(DetailedWeatherActivity.this, "Error parsing the data.", Toast.LENGTH_SHORT).show();
        }
    }
}

