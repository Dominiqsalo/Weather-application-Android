package com.example.myweatherapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.myweatherapp.CityAdapter;
import com.example.myweatherapp.R;

import java.util.ArrayList;
import java.util.List;

public class CitiesFragment extends Fragment {
    private CityDao cityDao;
    private RecyclerView recyclerView;
    private CityAdapter cityAdapter;
    private AppDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cities, container, false);

        if (db == null) {
            db = Room.databaseBuilder(getContext(), AppDatabase.class, "weather-app-db").build();
            cityDao = db.cityDao();
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        List<String> cityNames = new ArrayList<>();

        cityAdapter = new CityAdapter(getContext(), cityNames, new CityAdapter.CityItemClickListener() { // Assuming your CityAdapter accepts a List<String>
            @Override
            public void onCityClick(String cityName) {

                // Handle city click if needed. This is where you'd navigate to a details page or similar.
                Intent intent = new Intent(getActivity(), WeatherDisplayActivity.class);
                intent.putExtra("CITY_NAME", cityName);
                startActivity(intent);
            }

            @Override
            public void onDeleteCityClick(String cityName) {
                // Delete the city from the database and update the UI
                deleteCity(cityName);
                cityNames.remove(cityName);
                cityAdapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(cityAdapter);

        cityDao.getAllCities().observe(getViewLifecycleOwner(), cities -> {
            cityNames.clear();
            for (City city : cities) {
                cityNames.add(city.name);
            }
            cityAdapter.notifyDataSetChanged();
        });


        return view;
    }

    private void deleteCity(String cityName) {
        Thread thread = new Thread(() -> {
            City city = db.cityDao().findCityByName(cityName);
            if (city != null) {
                db.cityDao().delete(city);
            }
        });
        thread.start();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (db != null) {
            db.close();
            db = null;
        }
    }
}
