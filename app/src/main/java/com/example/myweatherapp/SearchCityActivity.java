package com.example.myweatherapp;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SearchCityActivity extends AppCompatActivity{
    private RecyclerView recyclerViewSearchResults;
    private EditText cityNameInput;   // Updated variable name
    private Button searchButton;
    private CityAdapter cityAdapter;
    private List<String> cityResults = new ArrayList<>();
    // Add other member variables as needed, like adapters, list data, etc.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        cityNameInput = findViewById(R.id.cityNameInput);
        searchButton = findViewById(R.id.searchButton);
        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);

        // Set up RecyclerView with adapter, data, etc.
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));
        cityAdapter = new CityAdapter(this, cityResults, new CityAdapter.CityItemClickListener() {
            @Override
            public void onCityClick(String cityName) {
                Toast.makeText(SearchCityActivity.this,"Selected: "+ cityName,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onDeleteCityClick(String cityName) {
                // Implementation for deleting a city from the search results
                // Since this might not be relevant for the search activity, you can leave it empty.
            }
        });
        recyclerViewSearchResults.setAdapter(cityAdapter);


        // Set up the onClick listener for the search button
        searchButton.setOnClickListener(view -> {
            String cityName = cityNameInput.getText().toString();
            if (!cityName.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("CITY_NAME", cityName);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        Button backButton = findViewById(R.id.back_buttonn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
