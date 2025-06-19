package com.example.myweatherapp.ui;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CityDao {
    @Query("SELECT * FROM City")
    LiveData<List<City>> getAllCities();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(City city);

    @Query("SELECT * FROM City WHERE city_name = :name")
    List<City> getCityByName(String name);

    @Query("SELECT * FROM City WHERE city_name = :cityName")
    City findCityByName(String cityName);
    @Delete
    void delete(City city);
}
